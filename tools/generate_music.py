"""Generate the project's original, sample-free, seamlessly wrapped PCM music loop."""
from array import array
from math import sin, pi, tanh
from pathlib import Path
import wave

RATE = 44100
BEAT = 0.6
BARS = 8
LENGTH = int(RATE * BEAT * 4 * BARS)
samples = array('d', [0.0]) * LENGTH


def note(midi, start, duration, gain, bass=False):
    frequency = 440.0 * 2 ** ((midi - 69) / 12)
    count = int(duration * RATE)
    origin = int(start * RATE)
    for i in range(count):
        t = i / RATE
        attack = min(1.0, t / 0.012)
        release = min(1.0, (duration - t) / 0.09)
        envelope = attack * release * (1 - t / duration) ** 1.4
        phase = 2 * pi * frequency * t
        tone = sin(phase) + (0.18 if bass else 0.3) * sin(2 * phase) + 0.08 * sin(3 * phase)
        samples[(origin + i) % LENGTH] += gain * envelope * tone


roots = [48, 45, 41, 43, 48, 45, 41, 43]
melodies = [
    [72, 76, 79, 76, 74, 76, 79, 76],
    [69, 72, 76, 72, 71, 72, 76, 72],
    [69, 72, 77, 72, 69, 72, 76, 72],
    [71, 74, 79, 74, 71, 74, 77, 74],
    [72, 76, 79, 81, 79, 76, 74, 76],
    [69, 72, 76, 79, 76, 72, 71, 72],
    [69, 72, 77, 79, 77, 72, 69, 72],
    [71, 74, 79, 77, 74, 71, 74, 71],
]
for bar in range(BARS):
    for beat in range(4):
        note(roots[bar] - 12, (bar * 4 + beat) * BEAT, BEAT * 0.9, 0.12, bass=True)
    for step, pitch in enumerate(melodies[bar]):
        note(pitch, (bar * 4 + step / 2) * BEAT, BEAT * 0.82, 0.105)

pcm = array('h', (round(tanh(value * 1.5) * 24000) for value in samples))
import sys
if sys.byteorder != 'little':
    pcm.byteswap()
output = Path(__file__).resolve().parents[1] / 'src/main/resources/resources/audio/meadow-loop.wav'
output.parent.mkdir(parents=True, exist_ok=True)
with wave.open(str(output), 'wb') as audio:
    audio.setnchannels(1)
    audio.setsampwidth(2)
    audio.setframerate(RATE)
    audio.writeframes(pcm.tobytes())
print(f'Generated {output.name}: {LENGTH / RATE:.1f}s, mono PCM 16-bit, {RATE}Hz')
