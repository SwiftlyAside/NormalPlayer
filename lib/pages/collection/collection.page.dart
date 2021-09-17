import 'package:flutter/material.dart';
import 'package:normal_player/components/page_nav_header.dart';
import 'package:normal_player/services/layout_service.dart';
import 'package:provider/provider.dart';

class CollectionsPage extends StatelessWidget {
  const CollectionsPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var layoutService = Provider.of<LayoutService>(context);
    return Column(
      children: [
        const PageNavHeader(pageIndex: 1),
        Flexible(
            child: PageView(
          physics: const AlwaysScrollableScrollPhysics(),
          controller: layoutService.pageServices[1].pageViewController,
          children: [
            Text('PlaylistsPage'), // TODO PlaylistsPage
            Text('FavoritesPage'), // TODO FavoritesPage
          ],
        ))
      ],
    );
  }
}
