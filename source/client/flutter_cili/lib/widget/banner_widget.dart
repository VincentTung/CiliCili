import 'package:flutter/material.dart';
import 'package:flutter_cili/model/home_data.dart';
import 'package:flutter_cili/model/video.dart';
import 'package:flutter_cili/navigator/navigator_controller.dart';
import 'package:flutter_cili/util/log_util.dart';
import 'package:flutter_swiper/flutter_swiper.dart';

class BannerWidget extends StatelessWidget {
  final List<BannerData> bannerList;
  final double bannerHeight;
  final EdgeInsetsGeometry padding;

  const BannerWidget(this.bannerList,
      {Key key,  this.bannerHeight = 160, this.padding})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: bannerHeight,
      child: _banner(),
    );
  }

  _banner() {
    var right = 10 + (padding?.horizontal ?? 0) / 2;

    return Swiper(
      itemCount: bannerList.length,
      autoplay: true,
      pagination: SwiperPagination(
        alignment: Alignment.bottomRight,
        margin: EdgeInsets.only(right: right,bottom: 10),
        builder: DotSwiperPaginationBuilder(
          color: Colors.white60,size: 8,activeSize: 10
        )
      ),
      itemBuilder: (BuildContext context, int index) {
        return _image(bannerList[index]);
      },
    );
  }

  _image(BannerData banner) {
    return InkWell(
      onTap: () {
       bannerClick(banner);
      },
      child: ClipRRect(
        borderRadius: BorderRadius.all(Radius.circular(6)),
        child: Image.network(banner.cover, fit: BoxFit.cover),
      ),
    );
  }


}
void bannerClick(BannerData banner) {

  if(banner.type == 'video'){
    NavigatorController.getInstance().onJumpTo(RouteStatus.detail,args: {'video':Video(vid:banner.url)});
  }else{
    NavigatorController.getInstance().openHtml(banner.url);
  }
}