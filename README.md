# ShredBench
ShredBench is a lightweight and fast HTTP server that can be used to test HTTP client performance. It was created to benchmark load generation tools, but might be useful for other purposes as well.

## Usage
To run ShredBench with default arguments, simply execute 
```bash
$ java -jar ShredBench.jar

Running ShredBench with the following options:
Running on port 8080
Response size: 1024 bytes

15:44:11.296 [DefaultDispatcher-worker-1] INFO ktor.application - Autoreload is disabled because the development mode is off.
15:44:11.311 [DefaultDispatcher-worker-1] INFO ktor.application - Application auto-reloaded in 0.014 seconds.
15:44:11.332 [main] INFO ktor.application - Responding at http://0.0.0.0:8080
```

Specify `-h` to see all usage options:
```bash
$ java -jar ShredBench.jar -h
Usage: ShredBench options_list
Options:
    --port, -p [8080] -> The port to handle HTTP requests on. { Int }
    --latency, -l -> A static latency value to inject before returning a response, in milliseconds. { Int }
    --data-size, -d [1024] -> The size of the response body to be returned, in bytes. { Int }
    --verbose, -v [false] -> Enable request logging on stdout. Note: this will reduce request handling throughput, but it's useful for debugging
    --help, -h -> Usage info
```

## Latency
ShredBench can introduce arbitrary latency when handling each request. To do so, specify the time to wait in milliseconds before sending a response via the `--latency` option.
