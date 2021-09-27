import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';

class AlbumsPage extends StatefulWidget {
  const AlbumsPage({Key? key}) : super(key: key);

  @override
  _AlbumsPageState createState() => _AlbumsPageState();
}

class _AlbumsPageState extends State<AlbumsPage> {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Text(
        "Albums",
        style: TextStyle(
          color: NormalTheme.kNormalThemeData.primaryColor,
          fontSize: 40,
          fontWeight: FontWeight.w700,
        ),
      ),
    );
  }
}
