import sounddevice as sd
import numpy as np

def audio_callback(indata, frames, time, status):
    volume_norm = np.linalg.norm(indata) * 10
    print(f"Audio Level: {volume_norm:.2f}")

# Start audio stream
with sd.InputStream(callback=audio_callback):
    print("Listening... Press Ctrl+C to stop")
    while True:
        pass
