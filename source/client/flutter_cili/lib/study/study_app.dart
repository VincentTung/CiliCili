import 'package:flutter/material.dart';
import 'package:flutter_bilibili/study/apple_page.dart';
import 'package:flutter_bilibili/study/banana_page.dart';
import 'package:flutter_bilibili/study/study_page.dart';

class VRouteDelegate extends RouterDelegate<NavigatorState>{
  @override
  void addListener(listener) {
    // TODO: implement addListener
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    throw UnimplementedError();
  }

  @override
  Future<bool> popRoute() {
    // TODO: implement popRoute
    throw UnimplementedError();
  }

  @override
  void removeListener(listener) {
    // TODO: implement removeListener
  }

  @override
  Future<void> setNewRoutePath(NavigatorState configuration) {
    // TODO: implement setNewRoutePath
    throw UnimplementedError();
  }
  
  
}

class StudyApp extends StatelessWidget {
  // This widget is the root of your application.

  static var router = <String, WidgetBuilder>{
    '/a': (BuildContext context) => ApplePage(),
    '/b': (BuildContext context) => BananaPage(),
  };

  final navigator = Navigator(
    initialRoute: null,
    onUnknownRoute: null,
    pages: [],
  );

  
  
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: 'Flutter Demo',
        routes: router,
        theme: ThemeData(
          // This is the theme of your application.
          //
          // Try running your application with "flutter run". You'll see the
          // application has a blue toolbar. Then, without quitting the app, try
          // changing the primarySwatch below to Colors.green and then invoke
          // "hot reload" (press "r" in the console where you ran "flutter run",
          // or simply save your changes to "hot reload" in a Flutter IDE).
          // Notice that the counter didn't reset back to zero; the application
          // is not restarted.
          primarySwatch: Colors.blue,
        ),
        home: StudyPage());
  }
}
