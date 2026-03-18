#!/usr/bin/env python3
"""
Final test script that should trigger read timeout by:
1. Sending headers + minimal data to start servlet
2. Waiting for servlet to read and need more data
3. Creating a long delay before sending remaining data
"""

import socket
import time

def test_read_timeout_final():
    host = 'localhost'
    port = 9080
    
    # Use 100KB total, send in very small initial chunk
    body_size = 100 * 1024
    body = b"A" * body_size
    
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
    
    # Disable Nagle's algorithm to send data immediately
    sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
    
    sock.connect((host, port))
    print("Connected!")
    
    # Send headers + only 1KB of data
    print("\nSending headers + 1KB...")
    sock.sendall(request_headers + body[:1024])
    print("Sent 1KB")
    
    # Small delay to let servlet start
    print("Waiting 1 second for servlet to start...")
    time.sleep(1)
    
    # Send another 1KB
    print("Sending another 1KB...")
    sock.sendall(body[1024:2048])
    print("Sent 2KB total")
    
    # NOW create the long delay - servlet should be blocked on read()
    print("\n*** Waiting 7 seconds (should trigger 5s read timeout) ***")
    time.sleep(7)
    
    # Try to send remaining data
    print("\nAttempting to send remaining data...")
    try:
        remaining = body[2048:]
        sock.sendall(remaining)
        print(f"Sent remaining {len(remaining)} bytes")
        
        sock.settimeout(5)
        response = sock.recv(4096)
        print("\nResponse received:")
        print(response.decode('utf-8', errors='ignore'))
    except socket.error as e:
        print(f"Socket error (connection likely closed by timeout): {e}")
    except Exception as e:
        print(f"Error: {e}")
    finally:
        sock.close()
        print("\nConnection closed")

if __name__ == "__main__":
    print("=" * 70)
    print("Final Read Timeout Test")
    print("=" * 70)
    print("\nThis sends minimal data upfront, then creates a 7-second gap")
    print("while the servlet is blocked waiting for more data.")
    print("=" * 70)
    print()
    
    test_read_timeout_final()

# Made with Bob
