mod generated;
use generated::hello::{say_client::SayClient, SayRequest};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut client = SayClient::connect("http://localhost:50051").await?;

    let request = tonic::Request::new(SayRequest { name: "foo".into() });

    let response = client.send(request).await?;

    println!("Antwort vom Server: {:?}", response.into_inner());

    Ok(())
}
