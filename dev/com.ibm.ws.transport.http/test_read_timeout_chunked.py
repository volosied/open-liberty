#!/usr/bin/env python3
"""
Test read timeout using chunked transfer encoding.
This forces multiple channelRead events with delays between them.
"""

import socket
import time

def test_read_timeout_chunked():
    host = 'localhost'
    port = 9080
    
    # Use chunked transfer encoding - no Content-Length
    request_headers = (
        f"POST /persistonerror/upload HTTP/1.1\r\n"
        f"Host: {host}:{port}\r\n"
        f"Content-Type: application/octet-stream\r\n"
        f"Transfer-Encoding: chunked\r\n"
        f"Connection: close\r\n"
        f"\r\n"
    ).encode()
    
    print(f"Connecting to {host}:{port}...")
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
    sock.connect((host, port))
    print("Connected!")
    
    # Send headers
    print("\nSending headers...")
    sock.sendall(request_headers)
    print("Headers sent!")
    
    # Send first chunk (1KB)
    print("\nSending first chunk (1KB)...")
    chunk_data = b"A" * 1024
    chunk = f"{len(chunk_data):X}\r\n".encode() + chunk_data + b"\r\n"
    sock.sendall(chunk)
    print("First chunk sent")
    
    # Small delay
    time.sleep(0.5)
    
    # Send second chunk (1KB)
    print("\nSending second chunk (1KB)...")
    chunk_data = b"B" * 1024
    chunk = f"{len(chunk_data):X}\r\n".encode() + chunk_data + b"\r\n"
    sock.sendall(chunk)
    print("Second chunk sent")
    
    # NOW create the long delay before sending the final chunk
    print("\n*** Waiting 3 seconds (should trigger 1s read timeout) ***")
    time.sleep(3)
    
    # Try to send final chunk
    print("\nAttempting to send final chunk...")
    try:
        chunk_data = b"C" * 1024
        chunk = f"{len(chunk_data):X}\r\n".encode() + chunk_data + b"\r\n"
        sock.sendall(chunk)
        print("Final chunk sent")
        
        # Send terminating chunk
        sock.sendall(b"0\r\n\r\n")
        print("Terminating chunk sent")
        
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
    print("Chunked Transfer Encoding Read Timeout Test")
    print("=" * 70)
    print("\nThis uses chunked encoding to force multiple channelRead events")
    print("with a 3-second delay between chunks to trigger the 1s timeout.")
    print("=" * 70)
    print()
    
    test_read_timeout_chunked()

# Made with Bob
