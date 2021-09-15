import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';
import 'package:normal_player/services/layout_service.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:provider/provider.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Permission.storage
        .request()
        .then((value) => {if (!value.isGranted) print('not granted!')});

    return MultiProvider(
      providers: [Provider<LayoutService>(create: (_) => LayoutService())],
      child: MaterialApp(
        theme: ThemeData(
          primarySwatch:
              NormalTheme.createMaterialColor(const Color(0xff60bfbf)),
        ),
        debugShowCheckedModeBanner: false,
        home: Wrapper(
          child: Column(
            mainAxisSize: MainAxisSize.max,
            children: [
              Expanded(
                child: Text(
                  '${Permission.mediaLibrary.status}',
                  style: const TextStyle(fontSize: 8),
                ),
              ),
              Container(
                height: 60,
                color: const Color(0xff60bfbf),
              )
            ],
          ),
        ),
      ),
    );
  }
}

class Wrapper extends StatelessWidget {
  final Widget child;

  const Wrapper({Key? key, required this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var layoutService = Provider.of<LayoutService>(context);

    return SlidingUpPanel(
      panel: Container(),
      controller: layoutService.globalPanelController,
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
