#!/usr/bin/env python3
"""
Simple script to test read timeout by sending data then pausing.
Sends headers + some data, then waits 6 seconds before sending more.
"""

import socket
import time

def test_read_timeout():
    host = 'localhost'
    port = 9080
    
    # Simple body content
    body = b"A" * 100  # 100 bytes
    
    # Build HTTP request
    request = (
        f"POST /persistonerror/upload HTTP/1.1\r\n"
        f"Host: {host}:{port}\r\n"
        f"Content-Type: application/octet-stream\r\n"
        f"Content-Length: {len(body)}\r\n"
        f"Connection: close\r\n"
        f"\r\n"
    ).encode()
    
    print(f"Connecting to {host}:{port}...")
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))
    print("Connected!")
    
    # Send headers + first 50 bytes
    print("\nSending headers + first 50 bytes...")
    sock.sendall(request + body[:50])
    print("Sent 50 bytes")
    
    # Wait 6 seconds (should trigger 5s read timeout)
    print("\nWaiting 6 seconds (should trigger readTimeout=5s)...")
    time.sleep(6)
    
    # Try to send remaining data
    print("Sending remaining 50 bytes...")
    try:
        sock.sendall(body[50:])
        print("Sent remaining 50 bytes")
        
        # Try to read response
        print("\nWaiting for response...")
        # sock.settimeout(5)
        response = sock.recv(4096)
        print("Response received:")
        print(response.decode('utf-8', errors='ignore'))
    except Exception as e:
        print(f"Error: {e}")
    finally:
        sock.close()
        print("\nConnection closed")

if __name__ == "__main__":
    print("=" * 60)
    print("Simple Read Timeout Test")
    print("=" * 60)
    test_read_timeout()

# Made with Bob
