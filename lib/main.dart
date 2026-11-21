
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'TP5 - Widget Glance',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.green),
        useMaterial3: true,
      ),
      home: const MesurePage(),
    );
  }
}

class MesurePage extends StatefulWidget {
  const MesurePage({super.key});

  @override
  State<MesurePage> createState() => _MesurePageState();
}

class _MesurePageState extends State<MesurePage> {
  static const platform = MethodChannel('widget/maj');

  double temperature = 36.5;
  double seuil = 40.0;

  int nombreWidgets = 0;
  bool chargement = false;
  String message = '';

  @override
  void initState() {
    super.initState();
    recupererNombreWidgets();
  }

  Future<void> recupererNombreWidgets() async {
    try {
      final nombre = await platform.invokeMethod<int>('nombreWidgets');

      if (!mounted) return;

      setState(() {
        nombreWidgets = nombre ?? 0;
      });
    } on PlatformException catch (e) {
      if (!mounted) return;
      setState(() {
        message = 'Erreur : ${e.message}';
      });
    }
  }

  Future<void> envoyerMesure() async {
    setState(() {
      chargement = true;
      message = '';
    });

    try {
      final nombre = await platform.invokeMethod<int>(
        'mettreAJour',
        {
          'valeur': temperature,
          'titre': 'Ruche · température',
          'unite': '°C',
          'seuil': seuil,
        },
      );

      if (!mounted) return;

      setState(() {
        nombreWidgets = nombre ?? 0;
        message = 'Mesure envoyée avec succès !';
      });
    } on PlatformException catch (e) {
      if (!mounted) return;

      setState(() {
        message = 'Erreur : ${e.message}';
      });
    } finally {
      if (mounted) {
        setState(() {
          chargement = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mesures de la ruche'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Icon(
              Icons.thermostat,
              size: 65,
              color: Colors.deepOrange,
            ),
            const SizedBox(height: 12),

            const Text(
              'Température actuelle',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 20),
            ),

            Text(
              '${temperature.toStringAsFixed(1)} °C',
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontSize: 36,
                fontWeight: FontWeight.bold,
              ),
            ),

            Slider(
              value: temperature,
              min: 0,
              max: 50,
              divisions: 100,
              label: '${temperature.toStringAsFixed(1)} °C',
              onChanged: (value) {
                setState(() {
                  temperature = value;
                });
              },
            ),

            const Divider(height: 35),

            const Text(
              'Seuil de température',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 18),
            ),

            Text(
              '${seuil.toStringAsFixed(1)} °C',
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontSize: 26,
                fontWeight: FontWeight.bold,
                color: Colors.orange,
              ),
            ),

            Slider(
              value: seuil,
              min: 0,
              max: 50,
              divisions: 100,
              label: '${seuil.toStringAsFixed(1)} °C',
              onChanged: (value) {
                setState(() {
                  seuil = value;
                });
              },
            ),

            const SizedBox(height: 20),

            FilledButton.icon(
              onPressed: chargement ? null : envoyerMesure,
              icon: chargement
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                      ),
                    )
                  : const Icon(Icons.send),
              label: Text(
                chargement ? 'Envoi...' : 'Envoyer au Widget',
              ),
            ),

            const SizedBox(height: 20),

            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    const Text(
                      'Widgets installés',
                      style: TextStyle(fontSize: 18),
                    ),
                    Text(
                      '$nombreWidgets',
                      style: const TextStyle(
                        fontSize: 32,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    TextButton(
                      onPressed: recupererNombreWidgets,
                      child: const Text('Actualiser'),
                    ),
                  ],
                ),
              ),
            ),

            if (message.isNotEmpty) ...[
              const SizedBox(height: 12),
              Text(
                message,
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: message.startsWith('Erreur')
                      ? Colors.red
                      : Colors.green,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
