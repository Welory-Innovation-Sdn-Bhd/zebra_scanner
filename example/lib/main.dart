import 'package:flutter/material.dart';
import 'dart:async';

import 'package:zebra_scanner/zebra_scanner.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? hello;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    ZebraScanner.init();
    ZebraScanner.onBardcodeScanned.listen((element) {
      setState(() {
        data = element.code;
      });
    });
  }

  String? data;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [Text(data ?? "NO DATA")],
          ),
        ),
      ),
    );
  }
}
