num = 258
# binary form: 258 = 256 + 2 = 00000001 00000000 + 00000000 00000010 = 00000001 00000010
print(num)
print(format(num, '016b'))

print("2-byte int, big endian")
print(num.to_bytes(2, 'big'))
print("2-byte int, little endian")
print(num.to_bytes(2, 'little'))

print("4-byte int, big endian")
print(num.to_bytes(4, 'big'))
print("4-byte int, little endian")
print(num.to_bytes(4, 'little'))
