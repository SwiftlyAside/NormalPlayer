import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:normal_player/services/layout_service.dart';
import 'package:provider/provider.dart';

class PageNavHeaderItem extends StatelessWidget {
  final String title;
  final int index;
  final int pageIndex;

  const PageNavHeaderItem(
      {Key? key,
      required this.title,
      required this.index,
      required this.pageIndex})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    var layoutService = Provider.of<LayoutService>(context);
    return StreamBuilder<double>(
      stream: layoutService.pageServices[pageIndex].pageIndex$,
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return Container();
        }
        final double pageValue = snapshot.data!;

        double opacity = 0.24;
        int floor = pageValue.floor();
        int ceil = pageValue.ceil();

        if (index == ceil && index == floor) {
          opacity = 1;
        } else {
          double dx = (ceil - pageValue);

          if (index == floor) {
            opacity = math.max(dx, 0.24);
          }
          if (index == ceil) {
            opacity = math.max(1 - dx, 0.24);
          }
        }

        WidgetsBinding.instance!.addPostFrameCallback(
            (_) => layoutService.pageServices[pageIndex].setSize(index));
        return Container(
          padding: const EdgeInsets.only(right: 10.0),
          alignment: Alignment.centerLeft,
          child: Text(
            title,
            style: TextStyle(
              color: Colors.white.withOpacity(opacity),
              fontSize: 22,
              fontWeight: FontWeight.bold,
            ),
          ),
        );
      },
    );
  }
}
