import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

void showWarningToast(String msg) {
  Fluttertoast.showToast(
      msg: msg,
      toastLength: Toast.LENGTH_LONG,
      gravity: ToastGravity.CENTER,
      textColor: Colors.white,
      backgroundColor: Colors.red);
}

void showToast(String msg) {
  Fluttertoast.showToast(
      msg: msg, toastLength: Toast.LENGTH_LONG, gravity: ToastGravity.CENTER);
}
