Currently uses a static seed -> will allways generate exactly the same data on every computer.

To load db from cache, use the static constructor, to save it, use save method.
DB is gzip compressed, because it reduces the size by 50%