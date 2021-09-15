import 'package:flutter/cupertino.dart';
import 'package:normal_player/values/lists.dart';
import 'package:rxdart/rxdart.dart';

class PageService {
  // Sub PageView
  late BehaviorSubject<double> _pageIndex$;

  BehaviorSubject<double> get pageIndex$ => _pageIndex$;
  late PageController _pageController;

  PageController get pageViewController => _pageController;

  // Header navigation bar
  late ScrollController _headerController;

  ScrollController get headerController => _headerController;
  late double _offset;
  late double _width;
  late List<double> _navSizes;
  late List<double> _cumulativeNavSizes;
  late bool _isSet;
  late int _setCount;

  List<double> get navSizes => _navSizes;

  List<double> get cumulativeNavSizes => _cumulativeNavSizes;
  final int id;

  PageService(this.id) {
    _initHeaderNavBar();
    _initPageView();
    _registerListeners();
  }

  void updatePageIndex(double value) {
    _pageIndex$.add(value);
  }

  void setSize(int index) {
    if (_isSet) {
      return;
    }
    GlobalKey key = headerItems[id]![index].value;
    RenderBox renderBoxRed =
        key.currentContext!.findRenderObject() as RenderBox;
    double width = renderBoxRed.size.width;
    _navSizes[index] = width;
    _setCount = _setCount + 1;
    _checkSet();
  }

  void _checkSet() {
    if (_setCount == headerItems[id]!.length) {
      _isSet = true;
      _constructCumulative();
    }
  }

  _constructCumulative() {
    _cumulativeNavSizes.add(0);
    _cumulativeNavSizes.add(_navSizes[0]);
    _navSizes.reduce((a, b) {
      _cumulativeNavSizes.add(a + b);
      return a + b;
    });
  }

  _initPageView() {
    _pageIndex$ = BehaviorSubject<double>.seeded(0);
    _pageController = PageController();
  }

  _initHeaderNavBar() {
    _headerController = ScrollController();
    _width = _offset = 0;
    _setCount = 0;
    _isSet = false;
    _navSizes = List.filled(headerItems[id]!.length, 0);
    _cumulativeNavSizes = <double>[];
  }

  void _registerListeners() {
    _pageController.addListener(() {
      int floor = _pageController.page!.floor();
      _pageIndex$.add(_pageController.page!);
      _offset = _cumulativeNavSizes[floor];
      _width = _navSizes[floor];
      _headerController
          .jumpTo((_pageController.page! - floor).abs() * _width + _offset);
    });
  }

  void dispose() {
    _pageController.dispose();
    _headerController.dispose();
  }
}
