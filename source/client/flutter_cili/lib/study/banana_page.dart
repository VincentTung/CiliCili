import 'package:flutter/material.dart';

class BananaPage extends StatefulWidget {
  @override
  _BananaPageState createState() => _BananaPageState();
}

class _BananaPageState extends State<BananaPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Container(
        child: Column(
          children: [Text('banana'),TextButton(onPressed: (){

            Navigator.of(context).pop();
          }, child: Text('finish'))],
        ),
      ),
    );
  }
}
