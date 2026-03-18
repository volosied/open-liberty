import http.client
import time

conn = http.client.HTTPConnection('localhost', 9080)

# First request
print("Sending first request...")
conn.request('POST', '/persistonerror/upload', body='test1')
response1 = conn.getresponse()
print(f"First response: {response1.status}")
response1.read()  # Consume response

time.sleep(0.5)

# Second request on same connection (firstRequest will be false)
print("Sending second request...")
conn.request('POST', '/persistonerror/upload', body='test2')
response2 = conn.getresponse()
print(f"Second response: {response2.status}")
response2.read()

conn.close()