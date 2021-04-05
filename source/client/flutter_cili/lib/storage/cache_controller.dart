import 'package:shared_preferences/shared_preferences.dart';

class CacheController {
  SharedPreferences sharedPreferences;

  static CacheController _instance;

  CacheController._() {
    init();
  }

  static CacheController getInstance() {
    if (_instance == null) {
      _instance = CacheController._();
    }

    return _instance;
  }

  void init() async {
    if (sharedPreferences == null) {
      sharedPreferences = await SharedPreferences.getInstance();
    }
  }

  static Future<CacheController> preInit() async {
    if (_instance == null) {
      var prefs = await SharedPreferences.getInstance();
      _instance = CacheController._pre(prefs);
    }
    return _instance;
  }

  CacheController._pre(SharedPreferences prefs) {
    this.sharedPreferences = prefs;
  }

  setString(String key, String value) {
    sharedPreferences.setString(key, value);
  }

  String getString(String key) {
    return sharedPreferences.getString(key);
  }

  setDouble(String key, double value) {
    sharedPreferences.setDouble(key, value);
  }

  double getDouble(String key) {
    return sharedPreferences.getDouble(key);
  }

  setStringList(String key, List<String> value) {
    sharedPreferences.setStringList(key, value);
  }

  List<String> getStringList(String key) {
    return sharedPreferences.getStringList(key);
  }

  T get<T>(String key) {
    return sharedPreferences.get(key);
  }

  void delete(String key){
    sharedPreferences.remove(key);
  }
}
