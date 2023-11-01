```shell
curl --location 'http://localhost:8081/produce' \
--header 'x-debug-trace: true' \
--header 'Content-Type: application/json' \
--data '{"data":"some data"}'
```