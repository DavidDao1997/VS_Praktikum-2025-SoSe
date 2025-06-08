fn main() {
    prost_build::Config::new()
        .out_dir("src/proto") // Zielverzeichnis für generierten Code
        .compile_protos(&["proto/hello.proto"], &["proto/"])
        .unwrap();
}
