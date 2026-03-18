#!/usr/bin/env python3
"""
Test READ timeout by simulating a network stall during upload.
This sends some data, then pauses at the TCP socket level (not just slow sending).
"""

import socket
import time
import sys

def test_read_timeout_with_stall():
    """
    Send partial request body, then stall to trigger READ timeout.
    """
    host = 'localhost'
    port = 9080
    
    # Create socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))
    
    print(f"Connected to {host}:{port}")
    
    # Prepare multipart form data
    boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"
    
    # Build the request headers
    headers = (
        f"POST /persistonerror/upload HTTP/1.1\r\n"
        f"Host: {host}:{port}\r\n"
        f"Content-Type: multipart/form-data; boundary={boundary}\r\n"
        f"Content-Length: 1000\r\n"  # Claim we'll send 1000 bytes
        f"Connection: keep-alive\r\n"
        f"\r\n"
    )
    
    # Send headers
    print("Sending headers...")
    sock.sendall(headers.encode())
    time.sleep(0.5)
    
    # Send partial body (just the boundary start)
    partial_body = f"--{boundary}\r\n"
    print(f"Sending partial body ({len(partial_body)} bytes)...")
    sock.sendall(partial_body.encode())
    
    # Now STALL - don't send any more data
    # The server expects 1000 bytes total but we've only sent ~40 bytes
    print("\n=== STALLING - Not sending any more data ===")
    print("Server should timeout after readTimeout (5 seconds)")
    print("Waiting for server response or timeout...")
    
    # Set socket to non-blocking to check for response
    sock.settimeout(10.0)
    
    try:
        response = sock.recv(4096)
        print(f"\nReceived response ({len(response)} bytes):")
        print(response.decode('utf-8', errors='replace'))
    except socket.timeout:
        print("\nNo response received (socket timeout)")
    except Exception as e:
        print(f"\nException: {e}")
    finally:
        sock.close()
        print("\nConnection closed")

def test_chunked_stall():
    """
    Send chunked request, then stall between chunks to trigger READ timeout.
    """
    host = 'localhost'
    port = 9080
    
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))
    
    print(f"\n\n=== TEST 2: Chunked Transfer with Stall ===")
    print(f"Connected to {host}:{port}")
    
    # Build request with chunked encoding
    headers = (
        f"POST /persistonerror/upload HTTP/1.1\r\n"
        f"Host: {host}:{port}\r\n"
        f"Content-Type: multipart/form-data; boundary=----WebKitFormBoundary\r\n"
        f"Transfer-Encoding: chunked\r\n"
        f"Connection: keep-alive\r\n"
        f"\r\n"
    )
    
    print("Sending headers with Transfer-Encoding: chunked...")
    sock.sendall(headers.encode())
    time.sleep(0.5)
    
    # Send first chunk
    chunk1 = "20\r\n" + ("A" * 32) + "\r\n"  # 32 bytes of data
    print(f"Sending first chunk (32 bytes)...")
    sock.sendall(chunk1.encode())
    
    # Now STALL - don't send the next chunk or the terminating chunk
    print("\n=== STALLING - Not sending next chunk ===")
    print("Server should timeout after readTimeout (5 seconds)")
    print("Waiting for server response or timeout...")
    
    sock.settimeout(10.0)
    
    try:
        response = sock.recv(4096)
        print(f"\nReceived response ({len(response)} bytes):")
        print(response.decode('utf-8', errors='replace'))
    except socket.timeout:
        print("\nNo response received (socket timeout)")
    except Exception as e:
        print(f"\nException: {e}")
    finally:
        sock.close()
        print("\nConnection closed")

if __name__ == "__main__":
    print("=" * 60)
    print("Testing READ timeout with network stall")
    print("=" * 60)
    
    try:
        # Test 1: Content-Length with incomplete body
        test_read_timeout_with_stall()
        
        time.sleep(2)
        
        # Test 2: Chunked encoding with missing chunks
        test_chunked_stall()
        
    except KeyboardInterrupt:
        print("\n\nTest interrupted by user")
    except Exception as e:
        print(f"\n\nTest failed with error: {e}")
        import traceback
        traceback.print_exc()

# Made with Bob
