import 'package:flutter/material.dart';
import 'package:normal_player/services/page_service.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

class LayoutService {
  // Main PageView
  late PageController _globalPageController;

  PageController get globalPageController => _globalPageController;

  // Sub PageViews
  late List<PageService> _pageServices;
  List<PageService> get pageServices => _pageServices;

  // Main Panel
  late PanelController _globalPanelController;

  PanelController get globalPanelController => _globalPanelController;

  LayoutService() {
    _initGlobalPageView();
    _initSubPageViews();
    _initGlobalPanel();
  }

  void _initGlobalPageView() => _globalPageController = PageController();

  void _initSubPageViews() {
    _pageServices = List.empty(growable: true);
    for (var i = 0; i < 2; i++) {
      _pageServices.add(PageService(i));
    }
  }

  void _initGlobalPanel() => _globalPanelController = PanelController();

  void changeGlobalPage(int pageIndex) {
    Curve curve = Curves.fastLinearToSlowEaseIn;
    _globalPageController.animateToPage(
      pageIndex,
      duration: const Duration(milliseconds: 200),
      curve: curve,
    );
  }
}
