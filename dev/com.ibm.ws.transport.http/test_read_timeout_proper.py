#!/usr/bin/env python3
"""
Script to properly test read timeout by sending data in chunks
with delays AFTER the servlet starts reading.

This uses a large enough file that the servlet will exhaust the initial
buffer and need to wait for more data from the network.
"""

import socket
import time
import sys

def test_read_timeout_properly():
    host = 'localhost'
    port = 9080
    
    # Use a larger body - 50KB
    # This is large enough that it won't all fit in one network buffer
    body_size = 50 * 1024  # 50KB
    chunk_size = 1024  # Send 1KB at a time
    
    body = b"A" * body_size
    
    # Build HTTP request headers
    request_headers = (
        f"POST /persistonerror/upload HTTP/1.1\r\n"
        f"Host: {host}:{port}\r\n"
        f"Content-Type: application/octet-stream\r\n"
        f"Content-Length: {body_size}\r\n"
        f"Connection: close\r\n"
        f"\r\n"
    ).encode()
    
    print(f"Connecting to {host}:{port}...")
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))
    print("Connected!")
    
    # Send headers immediately
    print("\nSending headers...")
    sock.sendall(request_headers)
    print("Headers sent!")
    
    # Send first few chunks quickly to get servlet started
    print(f"\nSending first 5KB quickly to start servlet...")
    for i in range(5):
        chunk = body[i*chunk_size:(i+1)*chunk_size]
        sock.sendall(chunk)
        print(f"  Sent chunk {i+1}: {len(chunk)} bytes")
        time.sleep(0.1)  # Small delay
    
    total_sent = 5 * chunk_size
    print(f"Sent {total_sent} bytes so far")
    
    # Now wait to let servlet start reading and exhaust buffer
    print("\nWaiting 2 seconds for servlet to start reading...")
    time.sleep(2)
    
    # Send a few more chunks
    print("\nSending next 5KB...")
    for i in range(5, 10):
        chunk = body[i*chunk_size:(i+1)*chunk_size]
        sock.sendall(chunk)
        print(f"  Sent chunk {i+1}: {len(chunk)} bytes")
        time.sleep(0.1)
    
    total_sent = 10 * chunk_size
    print(f"Sent {total_sent} bytes so far")
    
    # NOW create the long delay that should trigger timeout
    print("\n*** Waiting 7 seconds (should trigger 5s read timeout) ***")
    time.sleep(7)
    
    # Try to send remaining data
    print("\nAttempting to send remaining data...")
    try:
        remaining = body[total_sent:]
        sock.sendall(remaining)
        print(f"Sent remaining {len(remaining)} bytes")
        
        # Try to read response
        print("\nWaiting for response...")
        sock.settimeout(5)
        response = sock.recv(4096)
        print("Response received:")
        print(response.decode('utf-8', errors='ignore'))
    except socket.error as e:
        print(f"Socket error (expected if timeout occurred): {e}")
    except Exception as e:
        print(f"Error: {e}")
    finally:
        sock.close()
        print("\nConnection closed")

if __name__ == "__main__":
    print("=" * 70)
    print("Proper Read Timeout Test")
    print("=" * 70)
    print("\nThis test:")
    print("1. Sends headers + initial data quickly")
    print("2. Waits for servlet to start reading")
    print("3. Sends more data")
    print("4. Creates 7-second gap (> 5s readTimeout)")
    print("5. Tries to send remaining data")
    print("\nThe servlet should hit read timeout during step 4.")
    print("=" * 70)
    print()
    
    test_read_timeout_properly()

# Made with Bob
