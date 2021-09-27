import 'package:flutter/material.dart';
import 'package:normal_player/components/page_nav_header.dart';
import 'package:normal_player/pages/library/albums.page.dart';
import 'package:normal_player/pages/library/artists.page.dart';
import 'package:normal_player/pages/library/tracks.page.dart';
import 'package:normal_player/services/layout_service.dart';
import 'package:provider/provider.dart';

class LibraryPage extends StatelessWidget {
  const LibraryPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var layoutService = Provider.of<LayoutService>(context);
    return Column(
      children: [
        const PageNavHeader(pageIndex: 0),
        Flexible(
            child: PageView(
          physics: const AlwaysScrollableScrollPhysics(),
          controller: layoutService.pageServices[0].pageViewController,
          children: const [
            TracksPage(),
            ArtistsPage(),
            AlbumsPage(),
          ],
        ))
      ],
    );
  }
}
