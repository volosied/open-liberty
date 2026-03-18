#!/usr/bin/env python3
"""
Proper READ timeout test - sends headers, then stalls before body.
This ensures channelRead fires for headers, switching to READ phase,
then the stall triggers the READ timeout.
"""

import socket
import time
import sys

def test_read_timeout_with_header_body_gap():
    """
    Send headers, wait for server to process, then stall on body.
    """
    host = '9.46.80.217'
    port = 9080
    
    print(f"Connecting to {host}:{port}...")
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    # Disable Nagle's algorithm to send immediately
    sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
    
    sock.connect((host, port))
    print("✓ Connected!")
    
    # Prepare request
    body_size = 10000
    headers = (
        f"POST /persistonerror/upload HTTP/1.1\r\n"
        f"Host: {host}:{port}\r\n"
        f"Content-Type: application/octet-stream\r\n"
        f"Content-Length: {body_size}\r\n"
        f"Connection: keep-alive\r\n"
        f"\r\n"
    )
    
    # Step 1: Send headers only
    print("\n[Step 1] Sending HTTP headers...")
    sock.sendall(headers.encode())
    print(f"✓ Sent {len(headers)} bytes of headers")
    
    # Give server time to process headers and switch to READ phase
    print("\n[Step 2] Waiting 0.5s for server to process headers...")
    time.sleep(0.5)
    print("✓ Server should now be in READ phase, waiting for body")
    
    # Step 3: Send a tiny bit of body to confirm we're in READ phase
    print("\n[Step 3] Sending first 10 bytes of body...")
    sock.send(b"A" * 10)
    print("✓ Sent 10 bytes")
    
    # Step 4: STALL - don't send any more data
    print("\n[Step 4] === STALLING ===")
    print(f"Server expects {body_size} bytes total, but we've only sent 10")
    print("Server should timeout after readTimeout (5 seconds for you, 1s in logs)")
    print("\nWaiting for timeout...")
    
    # Wait and try to read response
    sock.settimeout(10.0)
    
    start_time = time.time()
    try:
        response = sock.recv(4096)
        elapsed = time.time() - start_time
        
        if response:
            print(f"\n✓ Received response after {elapsed:.1f}s ({len(response)} bytes):")
            print("-" * 60)
            print(response.decode('utf-8', errors='replace'))
            print("-" * 60)
        else:
            print(f"\n✗ Connection closed by server after {elapsed:.1f}s (no response)")
            
    except socket.timeout:
        elapsed = time.time() - start_time
        print(f"\n✗ Socket timeout after {elapsed:.1f}s (no response from server)")
    except ConnectionResetError as e:
        elapsed = time.time() - start_time
        print(f"\n✓ Connection reset by server after {elapsed:.1f}s")
        print(f"   This is expected - server closed due to timeout")
    except Exception as e:
        elapsed = time.time() - start_time
        print(f"\n✗ Exception after {elapsed:.1f}s: {type(e).__name__}: {e}")
    finally:
        sock.close()
        print("\n✓ Connection closed")

def test_chunked_stall():
    """
    Test with chunked encoding - send first chunk, then stall.
    """
    host = 'localhost'
    port = 9080
    
    print("\n\n" + "=" * 70)
    print("TEST 2: Chunked Transfer Encoding with Stall")
    print("=" * 70)
    
    print(f"\nConnecting to {host}:{port}...")
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
    sock.connect((host, port))
    print("✓ Connected!")
    
    # Send headers with chunked encoding
    headers = (
        f"POST /persistonerror/upload HTTP/1.1\r\n"
        f"Host: {host}:{port}\r\n"
        f"Content-Type: application/octet-stream\r\n"
        f"Transfer-Encoding: chunked\r\n"
        f"Connection: keep-alive\r\n"
        f"\r\n"
    )
    
    print("\n[Step 1] Sending headers with Transfer-Encoding: chunked...")
    sock.sendall(headers.encode())
    print("✓ Headers sent")
    
    time.sleep(0.5)
    
    # Send first chunk (32 bytes)
    chunk1 = "20\r\n" + ("B" * 32) + "\r\n"
    print("\n[Step 2] Sending first chunk (32 bytes)...")
    sock.sendall(chunk1.encode())
    print("✓ First chunk sent")
    
    # STALL - don't send next chunk or terminating chunk (0\r\n\r\n)
    print("\n[Step 3] === STALLING ===")
    print("Not sending next chunk or terminating chunk")
    print("Server should timeout after readTimeout")
    print("\nWaiting for timeout...")
    
    sock.settimeout(10.0)
    start_time = time.time()
    
    try:
        response = sock.recv(4096)
        elapsed = time.time() - start_time
        
        if response:
            print(f"\n✓ Received response after {elapsed:.1f}s:")
            print("-" * 60)
            print(response.decode('utf-8', errors='replace'))
            print("-" * 60)
        else:
            print(f"\n✗ Connection closed after {elapsed:.1f}s")
            
    except ConnectionResetError:
        elapsed = time.time() - start_time
        print(f"\n✓ Connection reset after {elapsed:.1f}s (timeout occurred)")
    except Exception as e:
        elapsed = time.time() - start_time
        print(f"\n✗ Exception after {elapsed:.1f}s: {e}")
    finally:
        sock.close()
        print("\n✓ Connection closed")

if __name__ == "__main__":
    print("=" * 70)
    print("READ Timeout Test - Proper Network Stall")
    print("=" * 70)
    print("\nThis test:")
    print("1. Sends HTTP headers (triggers switch to READ phase)")
    print("2. Sends tiny bit of body (confirms READ phase active)")
    print("3. Stalls (stops sending data)")
    print("4. Server should timeout after readTimeout")
    print("=" * 70)
    
    try:
        test_read_timeout_with_header_body_gap()
        
        time.sleep(2)
        
        test_chunked_stall()
        
    except KeyboardInterrupt:
        print("\n\nTest interrupted by user")
    except Exception as e:
        print(f"\n\nTest failed: {e}")
        import traceback
        traceback.print_exc()

# Made with Bob
