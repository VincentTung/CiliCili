import 'package:flutter/material.dart';
import 'package:flutter_bilibili/study/banana_page.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:flutter_bilibili/util/view_util.dart';

class StudyPage extends StatefulWidget {
  @override
  _StudyPageState createState() => _StudyPageState();
}

class _StudyPageState extends State<StudyPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Container(
        child: Column(
          children: [
            TextButton(
              onPressed: () {
                Navigator.pushNamed(context, '/a');
              },
              child: Text(
                'apple page',
                style: TextStyle(backgroundColor: primary, color: Colors.white),
              ),
            ),
            InkWell(
              onTap: () {
                _goBananaPage();
              },
              customBorder: borderLine(context),
              child: Text('banana page'),
            ),
          ],
        ),
      ),
    );
  }

  void _goBananaPage() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) {
        return BananaPage();
      }),
    );
  }
}
