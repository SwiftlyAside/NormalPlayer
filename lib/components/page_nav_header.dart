import 'package:flutter/material.dart';
import 'package:normal_player/components/page_nav_header_item.dart';
import 'package:normal_player/constants.dart';
import 'package:normal_player/services/layout_service.dart';
import 'package:normal_player/values/lists.dart';
import 'package:provider/provider.dart';

class PageNavHeader extends StatefulWidget {
  final int pageIndex;

  const PageNavHeader({Key? key, required this.pageIndex}) : super(key: key);

  @override
  _PageNavHeaderState createState() => _PageNavHeaderState();
}

class _PageNavHeaderState extends State<PageNavHeader> {
  @override
  Widget build(BuildContext context) {
    var layoutService = Provider.of<LayoutService>(context);
    return Container(
      color: NormalTheme.kNormalThemeData.primaryColor,
      height: 50,
      child: Row(
        children: [
          const Padding(
            padding: EdgeInsets.only(left: 53),
          ),
          Expanded(
              child: ListView.builder(
            padding: EdgeInsets.only(right: MediaQuery.of(context).size.width),
            physics: const NeverScrollableScrollPhysics(),
            controller:
                layoutService.pageServices[widget.pageIndex].headerController,
            itemBuilder: (context, index) {
              var items = headerItems[widget.pageIndex]!;
              return PageNavHeaderItem(
                  title: items[index].key.toUpperCase(),
                  key: items[index].value,
                  index: index,
                  pageIndex: widget.pageIndex);
            },
          ))
        ],
      ),
    );
  }
}
