import 'package:flutter/material.dart';

class ApplePage extends StatefulWidget {
  @override
  _ApplePageState createState() => _ApplePageState();
}

class _ApplePageState extends State<ApplePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Container(
        child: Column(children: [Text('apple')],),
      ),
    );
  }
}
