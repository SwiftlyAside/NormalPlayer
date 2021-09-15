class Tune {
  late String? id;
  late String title;
  late String artist;
  late String album;
  late int? duration;
  late String? uri;
  late String? albumArt;
  late List<int> colors;

  Tune(this.id, this.title, this.artist, this.album, this.duration, this.uri,
      this.albumArt, this.colors);

  Tune.fromMap(Map m) {
    id = m["id"];
    artist = m["artist"];
    title = m["title"];
    album = m["album"];
    duration = m["duration"];
    uri = m["uri"];
    albumArt = m["albumArt"];
    colors = m["colors"].cast<int>();
  }
}
