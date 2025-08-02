import 'package:shared_preferences/shared_preferences.dart';

class CacheController {
  late SharedPreferences sharedPreferences;

  static CacheController? _instance;

  CacheController._();

  static CacheController getInstance() {
    _instance ??= CacheController._();
    return _instance!;
  }

  Future<void> init() async {
    sharedPreferences = await SharedPreferences.getInstance();
  }

  static Future<CacheController> preInit() async {
    if (_instance == null) {
      var prefs = await SharedPreferences.getInstance();
      _instance = CacheController._pre(prefs);
    }
    return _instance!;
  }

  CacheController._pre(SharedPreferences prefs) {
    sharedPreferences = prefs;
  }

  setString(String key, String value) {
    sharedPreferences.setString(key, value);
  }

  String getString(String key) {
    return sharedPreferences.getString(key) ?? '';
  }

  setDouble(String key, double value) {
    sharedPreferences.setDouble(key, value);
  }

  double getDouble(String key) {
    return sharedPreferences.getDouble(key) ?? 0.0;
  }

  setInt(String key, int value) {
    sharedPreferences.setInt(key, value);
  }

  int getInt(String key) {
    return sharedPreferences.getInt(key) ?? 0;
  }

  setStringList(String key, List<String> value) {
    sharedPreferences.setStringList(key, value);
  }

  List<String> getStringList(String key) {
    return sharedPreferences.getStringList(key) ?? <String>[];
  }

  T? get<T>(String key) {
    return sharedPreferences.get(key) as T?;
  }

  void delete(String key) {
    sharedPreferences.remove(key);
  }

  bool getBool(String key) {
    if (sharedPreferences.containsKey(key)) {
      return sharedPreferences.getBool(key) ?? false;
    } else {
      return false;
    }
  }

  setBool(String key, bool value) {
    sharedPreferences.setBool(key, value);
  }
}
