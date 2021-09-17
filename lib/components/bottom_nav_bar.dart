import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';
import 'package:normal_player/services/layout_service.dart';
import 'package:normal_player/values/lists.dart';
import 'package:provider/provider.dart';

class BottomNavBar extends StatefulWidget {
  const BottomNavBar({Key? key}) : super(key: key);

  @override
  _BottomNavBarState createState() => _BottomNavBarState();
}

class _BottomNavBarState extends State<BottomNavBar> {
  int _currentIndex = 0;

  void _handleTap(int index, LayoutService layoutService) {
    switch (index) {
      case 0:
        layoutService.changeGlobalPage(index);
        _setBarIndex(index);
        break;
      case 1:
        layoutService.changeGlobalPage(index);
        _setBarIndex(index);
        break;
      case 2:
        _navigate();
        break;
      case 3:
        _navigate();
        break;
      case 4:
        _navigate();
        break;
    }
  }

  void _setBarIndex(int index) {
    setState(() {
      _currentIndex = index;
    });
  }

  void _navigate() {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => Container(), // TODO  SearchPage
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    var layoutService = Provider.of<LayoutService>(context);
    return BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) => _handleTap(index, layoutService),
        backgroundColor: NormalTheme.kNormalThemeData.primaryColorDark,
        unselectedItemColor: NormalTheme.kNormalThemeData.unselectedWidgetColor,
        selectedItemColor: NormalTheme.kNormalThemeData.selectedRowColor,
        type: BottomNavigationBarType.fixed,
        showUnselectedLabels: false,
        iconSize: 22,
        items: bottomNavBarItems
            .map((item) => BottomNavigationBarItem(
                backgroundColor: NormalTheme.kNormalThemeData.bottomAppBarColor,
                icon: item.value,
                label: item.key))
            .toList());
  }
}
