#!/usr/bin/env python3
"""
Extreme test: Send data byte-by-byte with socket buffer control
to force Netty to process data in multiple reads.
"""

import socket
import time
import struct

def test_read_timeout_extreme():
    host = 'localhost'
    port = 9080
    
    body = b"A" * 5000  # 5KB body
    
    request_headers = (
        f"POST /persistonerror/upload HTTP/1.1\r\n"
        f"Host: {host}:{port}\r\n"
        f"Content-Type: application/octet-stream\r\n"
        f"Content-Length: {len(body)}\r\n"
        f"Connection: close\r\n"
        f"\r\n"
    ).encode()
    
    print(f"Connecting to {host}:{port}...")
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    # Set very small send buffer to prevent buffering
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF, 512)
    sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
    
    sock.connect((host, port))
    print("Connected!")
    
    # Send headers
    print("\nSending headers...")
    sock.sendall(request_headers)
    print("Headers sent!")
    
    # Send first 100 bytes of body
    print("\nSending first 100 bytes...")
    sock.send(body[:100])
    time.sleep(0.1)
    
    # Send next 100 bytes
    print("Sending next 100 bytes...")
    sock.send(body[100:200])
    time.sleep(0.1)
    
    # Send next 100 bytes
    print("Sending next 100 bytes...")
    sock.send(body[200:300])
    
    # NOW create the long delay
    print("\n*** Waiting 2 seconds (should trigger 1s read timeout) ***")
    time.sleep(2)
    
    # Try to send remaining data
    print("\nAttempting to send remaining data...")
    try:
        remaining = body[300:]
        sock.sendall(remaining)
        print(f"Sent remaining {len(remaining)} bytes")
        
        # Try to read response
        sock.settimeout(5)
        response = sock.recv(4096)
        print("\nResponse received:")
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
    print("Extreme Read Timeout Test - Byte-by-Byte with Small Buffer")
    print("=" * 70)
    print("\nThis sends data in tiny chunks with small socket buffer")
    print("to force multiple network reads with a 2-second gap.")
    print("=" * 70)
    print()
    
    test_read_timeout_extreme()

# Made with Bob
