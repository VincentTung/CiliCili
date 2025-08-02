// import 'package:flutter/material.dart';
//
// void main() {
//   runApp(BooksApp());
// }
//
// class Book {
//   final String title;
//   final String author;
//
//   Book(this.title, this.author);
// }
//
// class BooksApp extends StatefulWidget {
//   @override
//   State<StatefulWidget> createState() => _BooksAppState();
// }
//
// class _BooksAppState extends State<BooksApp> {
//   BookRouterDelegate _routerDelegate = BookRouterDelegate();
//   BookRouteInformationParser _routeInformationParser =
//   BookRouteInformationParser();
//
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp.router(
//       title: 'Books App',
//       routerDelegate: _routerDelegate,
//       routeInformationParser: _routeInformationParser,
//     );
//   }
// }
//
// class BookRouteInformationParser extends RouteInformationParser<BookRoutePath> {
//   @override
//   Future<BookRoutePath> parseRouteInformation(
//       RouteInformation routeInformation) async {
//     final uri = Uri.parse(routeInformation.location);
//
//     if (uri.pathSegments.length >= 2) {
//       var remaining = uri.pathSegments[1];
//       return BookRoutePath.details(int.tryParse(remaining));
//     } else {
//       return BookRoutePath.home();
//     }
//   }
//
//   @override
//   RouteInformation restoreRouteInformation(BookRoutePath path) {
//     if (path.isHomePage) {
//       return RouteInformation(location: '/');
//     }
//     if (path.isDetailsPage) {
//       return RouteInformation(location: '/book/${path.id}');
//     }
//     return null;
//   }
// }
//
// class BookRouterDelegate extends RouterDelegate<BookRoutePath>
//     with ChangeNotifier, PopNavigatorRouterDelegateMixin<BookRoutePath> {
//   final GlobalKey<NavigatorState> navigatorKey;
//
//   Book _selectedBook;
//
//   List<Book> books = [
//     Book('Stranger in a Strange Land', 'Robert A. Heinlein'),
//     Book('Foundation', 'Isaac Asimov'),
//     Book('Fahrenheit 451', 'Ray Bradbury'),
//   ];
//
//   BookRouterDelegate() : navigatorKey = GlobalKey<NavigatorState>();
//
//   BookRoutePath get currentConfiguration => _selectedBook == null
//       ? BookRoutePath.home()
//       : BookRoutePath.details(books.indexOf(_selectedBook));
//
//   @override
//   Widget build(BuildContext context) {
//     return Navigator(
//       key: navigatorKey,
//       transitionDelegate: NoAnimationTransitionDelegate(),
//       pages: [
//         MaterialPage(
//           key: ValueKey('BooksListPage'),
//           child: BooksListScreen(
//             books: books,
//             onTapped: _handleBookTapped,
//           ),
//         ),
//         if (_selectedBook != null) BookDetailsPage(book: _selectedBook)
//       ],
//       onPopPage: (route, result) {
//         if (!route.didPop(result)) {
//           return false;
//         }
//
//         // Update the list of pages by setting _selectedBook to null
//         _selectedBook = null;
//         notifyListeners();
//
//         return true;
//       },
//     );
//   }
//
//   @override
//   Future<void> setNewRoutePath(BookRoutePath path) async {
//     if (path.isDetailsPage) {
//       _selectedBook = books[path.id];
//     }
//   }
//
//   void _handleBookTapped(Book book) {
//     _selectedBook = book;
//     notifyListeners();
//   }
// }
//
// class BookDetailsPage extends Page {
//   final Book book;
//
//   BookDetailsPage({
//     this.book,
//   }) : super(key: ValueKey(book));
//
//   Route createRoute(BuildContext context) {
//     return MaterialPageRoute(
//       settings: this,
//       builder: (BuildContext context) {
//         return BookDetailsScreen(book: book);
//       },
//     );
//   }
// }
//
// class BookRoutePath {
//   final int id;
//
//   BookRoutePath.home() : id = null;
//
//   BookRoutePath.details(this.id);
//
//   bool get isHomePage => id == null;
//
//   bool get isDetailsPage => id != null;
// }
//
// class BooksListScreen extends StatelessWidget {
//   final List<Book> books;
//   final ValueChanged<Book> onTapped;
//
//   BooksListScreen({
//     required this.books,
//     required this.onTapped,
//   });
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(),
//       body: ListView(
//         children: [
//           for (var book in books)
//             ListTile(
//               title: Text(book.title),
//               subtitle: Text(book.author),
//               onTap: () => onTapped(book),
//             )
//         ],
//       ),
//     );
//   }
// }
//
// class BookDetailsScreen extends StatelessWidget {
//   final Book book;
//
//   BookDetailsScreen({
//     required this.book,
//   });
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(),
//       body: Padding(
//         padding: const EdgeInsets.all(8.0),
//         child: Column(
//           crossAxisAlignment: CrossAxisAlignment.start,
//           children: [
//             if (book != null) ...[
//               Text(book.title, style: Theme.of(context).textTheme.headline6),
//               Text(book.author, style: Theme.of(context).textTheme.subtitle1),
//             ],
//           ],
//         ),
//       ),
//     );
//   }
// }
//
// class NoAnimationTransitionDelegate extends TransitionDelegate<void> {
//   @override
//   Iterable<RouteTransitionRecord> resolve({
//     List<RouteTransitionRecord> newPageRouteHistory,
//     Map<RouteTransitionRecord, RouteTransitionRecord>
//     locationToExitingPageRoute,
//     Map<RouteTransitionRecord, List<RouteTransitionRecord>>
//     pageRouteToPagelessRoutes,
//   }) {
//     final results = <RouteTransitionRecord>[];
//
//     for (final pageRoute in newPageRouteHistory) {
//       if (pageRoute.isWaitingForEnteringDecision) {
//         pageRoute.markForAdd();
//       }
//       results.add(pageRoute);
//     }
//
//     for (final exitingPageRoute in locationToExitingPageRoute.values) {
//       if (exitingPageRoute.isWaitingForExitingDecision) {
//         exitingPageRoute.markForRemove();
//         final pagelessRoutes = pageRouteToPagelessRoutes[exitingPageRoute];
//         if (pagelessRoutes != null) {
//           for (final pagelessRoute in pagelessRoutes) {
//             pagelessRoute.markForRemove();
//           }
//         }
//       }
//
//       results.add(exitingPageRoute);
//     }
//     return results;
//   }
// }