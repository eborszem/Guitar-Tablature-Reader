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
  <p>(Image from https://breakthroughguitar.com/striking-the-right-note-how-do-i-play-an-e-chord-on-guitar/)</p>
</p>
Every chord will have this numbering system. In the application, the below image roughly resembles the popup's appearance after you click, for example, an E chord:
<p>
<img src="https://github.com/user-attachments/assets/ebf4c1c1-19be-4773-850d-f0d307451061" width="500">
  <p>(Image from https://chordbank.com/chords/e-major/)</p>
</p>
Notice the placement of the dots/numbers is where the fingers go when you play an E chord.

(The E, A, D, G, B, and E notes on the left are the "zeroth fret" and are only there to clarify that the guitar is tuned to EADGBE.)
## What does this project do?
This project allows for users to create and play guitar tablature. Users can make songs, which are stored in a database and persist, and seamlessly switch between them. Measures can be added to a song by either using the button at the top of the page or by hovering over any measure and clicking the ```+``` symbol. Using ```+``` will add a blank measure immediately after the measure ```+``` was clicked on. Hovering over a measure also reveals a trash can symbol, to delete the measure, and a copy button, which duplicates the measure with all its chords/notes (the new measure is again placed immediately after the measure that was copied).

When loading a new song (or if there are no songs yet), the user sees a blank composition (except for one measure with a rest). You can add measures, which will be prepopulated with a rest (represented with ```X```s). When the user clicks on a chord (represented by a stack of numbers, as seen above with the E major chord), they will be shown a virtual fretboard. The virtual fretboard has the notes of the selected chord prefilled on all six guitar strings just as they would appear if you were physically playing a guitar. You can modify the chord's notes (by clicking anywhere on the fretboard) and duration. You can also choose to delete it entirely. A chord can easily turn into a rest by deselecting every note in the chord.

### Steps to run locally:
1. **Clone the repository:**
```bash
git clone https://github.com/eborszem/Guitar-Tablature-Reader.git
cd Guitar-Tablature-Reader
```
2. **Configure the database:**
- Update your database credentials in `application.properties` (located in website/demo/src/main/resources)

3. **Run the backend:**
```bash
cd website/demo
./mvnw spring-boot:run
 ```
4. **Access the app:**
- Open your browser at `http://localhost:8080/page`