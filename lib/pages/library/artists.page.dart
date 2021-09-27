import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';

class ArtistsPage extends StatefulWidget {
  const ArtistsPage({Key? key}) : super(key: key);

  @override
  _ArtistsPageState createState() => _ArtistsPageState();
}

class _ArtistsPageState extends State<ArtistsPage> {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Text(
        "Artists",
        style: TextStyle(
          color: NormalTheme.kNormalThemeData.primaryColor,
          fontSize: 40,
          fontWeight: FontWeight.w700,
        ),
      ),
    );
  }
}
