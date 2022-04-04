import 'dart:async';

import 'package:flutter/services.dart';

class ZebraScanner {
  static const MethodChannel _channel = MethodChannel('zebra_scanner');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  ZebraScanner() {
    setMethodCallHandler();
  }

  final StreamController<String> _controller = StreamController.broadcast();

  Stream<String> get onBardcodeScanned => _controller.stream;

  setMethodCallHandler() async {
    _channel.setMethodCallHandler((call) async {
      print("HERERERE");
      switch (call.method) {
        case 'onRecognizeBarcode':
          _controller.sink.add(call.arguments);
          break;
        default:
          break;
      }
    });
  }
}
