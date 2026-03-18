#!/usr/bin/env python3
"""
Script to test HTTP read timeout by sending a multipart upload slowly.
Sends headers quickly but delays the body to trigger read timeout.
"""

import socket
import time
import sys

def send_slow_upload(host='localhost', port=9080, chunk_delay=2.0):
    """
    Send a multipart form upload with delays between chunks to trigger read timeout.
    
    Args:
        host: Server hostname
        port: Server port
        chunk_delay: Delay in seconds between sending chunks (should be > readTimeout)
    """
    
    # Create the multipart boundary
    boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"
    
    # Create the file content (10KB of data)
    file_content = b"A" * 10240
    
    # Build the multipart body
    body_parts = []
    body_parts.append(f"--{boundary}\r\n".encode())
    body_parts.append(b'Content-Disposition: form-data; name="file"; filename="largefile.bin"\r\n')
    body_parts.append(b'Content-Type: application/octet-stream\r\n')
    body_parts.append(b'\r\n')
    body_parts.append(file_content)
    body_parts.append(b'\r\n')
    body_parts.append(f"--{boundary}--\r\n".encode())
    
    # Calculate total body length
    body = b''.join(body_parts)
    content_length = len(body)
    
    # Build HTTP headers
    headers = [
        f"POST /persistonerror/upload HTTP/1.1\r\n",
        f"Host: {host}:{port}\r\n",
        f"Content-Type: multipart/form-data; boundary={boundary}\r\n",
        f"Content-Length: {content_length}\r\n",
        "Connection: keep-alive\r\n",
        "\r\n"
    ]
    
    try:
        # Connect to server
        print(f"Connecting to {host}:{port}...")
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((host, port))
        print("Connected!")
        
        # Send headers immediately
        print("Sending headers...")
        for header in headers:
            sock.sendall(header.encode())
        print("Headers sent!")
        
        # Send body in chunks with delays
        chunk_size = 1024  # 1KB chunks
        total_sent = 0
        
        print(f"\nSending body in {chunk_size} byte chunks with {chunk_delay}s delay between chunks...")
        print(f"Total body size: {content_length} bytes")
        print(f"This should trigger read timeout if readTimeout < {chunk_delay}s\n")
        
        for i in range(0, len(body), chunk_size):
            chunk = body[i:i+chunk_size]
            sock.sendall(chunk)
            total_sent += len(chunk)
            
            print(f"Sent chunk {i//chunk_size + 1}: {len(chunk)} bytes (total: {total_sent}/{content_length})")
            
            # Don't delay after the last chunk
            if total_sent < content_length:
                print(f"Waiting {chunk_delay} seconds before next chunk...")
                time.sleep(chunk_delay)
        
        print(f"\nAll data sent! Total: {total_sent} bytes")
        
        # Try to receive response
        print("\nWaiting for response...")
        sock.settimeout(10)
        
        try:
            response = sock.recv(4096)
            print("\n=== Server Response ===")
            print(response.decode('utf-8', errors='ignore'))
        except socket.timeout:
            print("No response received (timeout)")
        except Exception as e:
            print(f"Error receiving response: {e}")
        
        sock.close()
        print("\nConnection closed")
        
    except ConnectionRefusedError:
        print(f"ERROR: Could not connect to {host}:{port}")
        print("Make sure the server is running!")
        sys.exit(1)
    except Exception as e:
        print(f"ERROR: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    print("=" * 60)
    print("HTTP Read Timeout Test Script")
    print("=" * 60)
    print()
    
    # Parse command line arguments
    host = sys.argv[1] if len(sys.argv) > 1 else 'localhost'
    port = int(sys.argv[2]) if len(sys.argv) > 2 else 9080
    delay = float(sys.argv[3]) if len(sys.argv) > 3 else 6.0
    
    print(f"Configuration:")
    print(f"  Host: {host}")
    print(f"  Port: {port}")
    print(f"  Chunk delay: {delay}s")
    print(f"  (Use: python3 {sys.argv[0]} [host] [port] [delay])")
    print()
    
    send_slow_upload(host, port, delay)

# Made with Bob
