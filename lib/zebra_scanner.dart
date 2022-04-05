import 'dart:async';

import 'package:flutter/services.dart';

class ZebraScanner {
  static const MethodChannel _channel = MethodChannel('zebra_scanner');

  static void init() {
    _channel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'onCodeDetected':
          if (call.arguments != null) {
            final _result = ZebraResult.fromPlatform(call.arguments);
            print(_result.code);
            _controller.sink.add(_result);
          }
          break;
        default:
          break;
      }
    });
  }

  static final StreamController<ZebraResult> _controller =
      StreamController.broadcast();

  static Stream<ZebraResult> get onBardcodeScanned => _controller.stream;
}

class ZebraResult {
  final String code;
  final String source;
  final String type;

  ZebraResult({required this.code, required this.source, required this.type});

  factory ZebraResult.fromPlatform(Map raw) {
    print(raw.toString());
    return ZebraResult(
        code: raw['data'], source: raw['source'], type: raw['type']);
  }
}
