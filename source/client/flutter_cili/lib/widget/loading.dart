import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';

class Loading extends StatelessWidget {
  final Widget child;
  final bool isLoading;
  final bool cover;

  const Loading({Key? key, required this.isLoading, this.cover = false, required this.child})
      : super(key: key);

  Widget get _loadingView {
    return Center(
      child: Lottie.asset('assets/loading.json'),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (cover) {
      return Stack(
        children: [child, isLoading ? _loadingView : Container()],
      );
    }
    return Container();
  }
}
