# Guitar Tablature Reader
## What is tablature?
Guitar tablature is how guitar music is read, like sheet music for a piano. There are six horizontal lines representing the six strings (EADGBE) on a guitar. Every number on these lines represents the frets your fingers would go on to play a chord.

So:
```
E | 0 (This is an E note)
B | 0 (B)
G | 1 (G#)
D | 2 (E)
A | 2 (B)
E | 0 (E)
```
is an E major chord. This is how it is played:
<p>
  <img src="https://i0.wp.com/breakthroughguitar.com/wp-content/uploads/2023/05/Hot-to-play-an-E-chord-on-guitar.png" width="500">
  <p>From https://breakthroughguitar.com/striking-the-right-note-how-do-i-play-an-e-chord-on-guitar/</p>
</p>

(The E, A, D, G, B, and E notes on the left are the "zeroth fret" and are only there to clarify that the guitar is tuned to EADGBE.)
## What does this project do?
When loading for the first time, a user sees a blank composition. You can add measures, which will be prepopulated with a rest (currently represented by negative numbers). When the user clicks on a chord shape like the one above (which can either be a chord/note or rest), they will be shown a fretboard, with the notes prefilled for convenience. You can modify the notes or duration of a chord. You can also turn it into a rest by double clicking a prefilled note.

This project allows for users to create and play guitar tablature. Users can seamlessly switch between songs, which are stored and persist. Creating a new song will load a new, blank composition.
