import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

void main() async {
  await Permission.mediaLibrary.status;
// setupLocator();

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        primarySwatch: NormalTheme.createMaterialColor(const Color(0xff60bfbf)),
      ),
      debugShowCheckedModeBanner: false,
      home: Wrapper(
        child: Column(
          mainAxisSize: MainAxisSize.max,
          children: [
            Expanded(
              child: Container(),
            ),
            Container(
              height: 60,
              color: const Color(0xff60bfbf),
            )
          ],
        ),
      ),
    );
  }
}

class Wrapper extends StatelessWidget {
  final Widget child;

  const Wrapper({Key? key, required this.child}) : super(key: key);

  // TODO locate layoutService.

  @override
  Widget build(BuildContext context) {
    return SlidingUpPanel(
      panel: Container(),
      // controller: layoutService.globalPanelController,
      minHeight: 60,
      maxHeight: MediaQuery.of(context).size.height,
      backdropEnabled: true,
      backdropOpacity: 0.5,
      parallaxEnabled: true,
      // collapsed: Material(
      //   child: BottomPanel(),
      // ),
      body: MaterialApp(
        debugShowCheckedModeBanner: false,
        home: child,
      ),
    );
  }
}
