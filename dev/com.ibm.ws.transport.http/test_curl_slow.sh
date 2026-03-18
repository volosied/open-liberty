#!/bin/bash
# Script to test read timeout with curl using a very small 8-byte upload
# This sends data slowly to trigger the read timeout

# Create a small 8-byte file
echo -n "12345678" > small_file.bin

echo "Testing read timeout with 8-byte file upload"
echo "File size: 8 bytes"
echo "Upload rate: 1 byte/second (should trigger 5s read timeout)"
echo ""

# Use curl with very slow rate (1 byte per second)
# With 8 bytes and 1 byte/sec, this takes 8 seconds
# Should trigger readTimeout=5s
curl -X POST \
  -H "Content-Type: application/octet-stream" \
  --limit-rate 1 \
  --data-binary @small_file.bin \
  -v \
  http://localhost:9080/persistonerror/upload

echo ""
echo "Upload complete (or timed out)"

# Made with Bob
