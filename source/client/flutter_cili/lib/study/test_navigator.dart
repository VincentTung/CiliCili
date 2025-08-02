// import 'dart:ffi';
//
// import 'package:flutter/material.dart';
// import 'package:flutter_bilibili/study/study_page.dart';
//
//
// class VRouteInfo {
//
//
//   String path;
//
//
//   VRouteInfo.home() : path = null;
//
//   VRouteInfo.page(path) : path =path;
//
// }
//
//
// class VTRouteInformationParser extends RouteInformationParser<VRouteInfo> {
//   @override
//   Future<VRouteInfo> parseRouteInformation(RouteInformation routeInformation) {
//     final uri = Uri.parse(routeInformation.location);
//     if (uri.pathSegments.length >= 2) {
//       var remaining = uri.pathSegments[1];
//       return BookRoutePath.details(int.tryParse(remaining));
//     } else {
//       return BookRoutePath.home();
//     }
//   }
//
//   @override
//   RouteInformation restoreRouteInformation(VRouteInfo configuration) {
//     return super.restoreRouteInformation(configuration);
//   }
// }
//
//
// class VTRouterDelegate extends RouterDelegate<VRouteInfo>
//     with ChangeNotifier, PopNavigatorRouterDelegateMixin<VRouteInfo> {
//   @override
//   Widget build(BuildContext context) {
//     return Navigator(
//
//       pages: [
//         MaterialPage(child: StudyPage())
//       ],
//     );
//   }
//
//   @override
//   // TODO: implement navigatorKey
//   GlobalKey<NavigatorState> get navigatorKey => throw UnimplementedError();
//
//   @override
//   Future<void> setNewRoutePath(VRouteInfo configuration) {
//     // TODO: implement setNewRoutePath
//     throw UnimplementedError();
//   }
//
// // @override
// // void addListener(listener) {
// //   // TODO: implement addListener
// // }
// //
// // @override
// // Future<bool> popRoute() {
// //   // TODO: implement popRoute
// //   throw UnimplementedError();
// // }
// //
// // @override
// // void removeListener(listener) {
// //   // TODO: implement removeListener
// // }
//
//
// }