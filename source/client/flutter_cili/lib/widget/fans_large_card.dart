import 'package:flutter/material.dart';
import 'package:flutter_bilibili/model/owner.dart';
import 'package:flutter_bilibili/util/view_util.dart';

class FansLargeCard extends StatelessWidget {
  final Owner owner;

  const FansLargeCard({super.key, required this.owner});


  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      child: Container(
          height: 106,
          decoration: BoxDecoration(border: borderLine(context)),
          margin: EdgeInsets.only(left: 15, right: 15, bottom: 15),
          child: Row(
            children: [_buildContent()],
          )),
      onTap: () {},
    );
  }

  _buildContent() {
    return Row(
      children: [
        Image.network(owner.face, width: 60,
          height: 60,),
        VSpace(width: 8),
        Container(
          padding: EdgeInsets.all(1),
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(2),
              border: Border.all(color: Colors.grey)),
          child: Text(
            'UP',
            style: TextStyle(
                fontSize: 8, color: Colors.grey, fontWeight: FontWeight.bold),
          ),
        ),
        VSpace(width: 8),
        Text(owner.name, style: TextStyle(fontSize: 11, color: Colors.grey)),

        VSpace(width: 8),

        Text("粉丝:${owner.fans}", style: TextStyle(fontSize: 11, color: Colors.grey)),
      ],
    );
  }


}
