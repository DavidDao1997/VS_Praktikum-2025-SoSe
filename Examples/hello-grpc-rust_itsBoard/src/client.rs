#![no_std]
#![no_main]

extern crate alloc;
use alloc::string::String;

use panic_halt as _;
use stm32f4xx_hal::{pac, prelude::*};

mod proto;
use crate::proto::hello::SayRequest;
use prost::Message;

use core::mem::MaybeUninit;
use cortex_m_rt::entry;
use linked_list_allocator::LockedHeap;

#[global_allocator]
static ALLOCATOR: LockedHeap = LockedHeap::empty();

// use smoltcp::iface::SocketHandle;
use smoltcp::iface::{Config, Interface, SocketSet};
use smoltcp::socket::tcp::{Socket, SocketBuffer};
use smoltcp::time::Instant;
use smoltcp::wire::{IpAddress, IpEndpoint, Ipv4Address};
use stm32_eth::dma::EthernetDMA;

#[entry]
fn main() -> ! {
    const HEAP_SIZE: usize = 1024;
    static mut HEAP: [MaybeUninit<u8>; HEAP_SIZE] = [MaybeUninit::uninit(); HEAP_SIZE];

    unsafe {
        ALLOCATOR.lock().init(HEAP.as_ptr() as *mut u8, HEAP_SIZE);
    }

    // Ethernet-MAC/PHY initialisieren (Details je nach HAL und PHY!)
    let dp = pac::Peripherals::take().unwrap();
    let cp = cortex_m::Peripherals::take().unwrap();

    // Initialisiere Clocks, GPIOs, RMII usw. (siehe stm32f4xx-hal und stm32-eth Doku)
    // let (eth_pins, clocks, ...) = ...;

    // Ethernet-Device erzeugen
    let mut eth = EthernetDMA::new(
        dp.ETHERNET_MAC,
        dp.ETHERNET_DMA,
        /* eth_pins, clocks, ... */
    );

    let mac_addr = smoltcp::wire::EthernetAddress([0x02, 0x00, 0x00, 0x00, 0x00, 0x01]);

    // smoltcp Interface anlegen
    let mut iface = Interface::new(Config::new(mac_addr), &mut eth, Instant::from_millis(0));

    // IP-Adresse konfigurieren (statisch)
    iface.update_ip_addrs(|addrs| {
        addrs
            .push(smoltcp::wire::IpCidr::new(
                IpAddress::v4(192, 168, 1, 123),
                24,
            ))
            .unwrap();
    });

    // Protobuf-Nachricht erzeugen
    let req = SayRequest {
        name: String::from("Alice"),
    };

    let mut buf = alloc::vec::Vec::new();
    req.encode(&mut buf).unwrap();

    // Netzwerk-Stack initialisieren (vereinfacht, Details je nach Board!)
    // Hier nur ein Beispiel für die Socket-Nutzung:
    static mut RX_DATA: [u8; 1024] = [0; 1024];
    static mut TX_DATA: [u8; 1024] = [0; 1024];

    let rx_buffer = unsafe { SocketBuffer::new(&mut RX_DATA[..]) };
    let tx_buffer = unsafe { SocketBuffer::new(&mut TX_DATA[..]) };
    let mut socket = Socket::new(rx_buffer, tx_buffer);

    // SocketSet mit einem Socket
    let mut sockets_storage = [None];
    let mut sockets = SocketSet::new(&mut sockets_storage);
    let handle = sockets.add(socket);

    // Lokalen gRPC-Endpunkt ansprechen (z.B. 127.0.0.1:50051)
    let remote_ip = IpAddress::v4(192, 168, 1, 10);
    let remote_port = 49500;

    let remote_endpoint = IpEndpoint::new(remote_ip, remote_port);
    // let remote_endpoint = (IpAddress::v4(127, 0, 0, 1), 50051);

    loop {
        iface.poll(Instant::from_millis(0), &mut eth, &mut sockets);

        let mut socket = sockets.get::<Socket>(handle);
        if !socket.is_active() && !socket.is_open() {
            socket.connect(remote_endpoint, 49501).unwrap();
        }
        if socket.is_active() && socket.can_send() {
            socket.send_slice(&buf).unwrap();
            break;
        }
    }

    loop {}
}
