# VS-Praktikum – SoSe 2025 – Verteilte Systeme

Teamprojekt im Rahmen des Moduls **Verteilte Systeme** an der HAW Hamburg.  
Ziel war die Entwicklung eines verteilten Systems zur Steuerung eines Roboterarms unter Einsatz geeigneter Architektur- und Entwurfsmuster.

## Zielsetzung
Das Projekt verfolgte das Ziel, ein funktionierendes, verteiltes Softwaresystem zu entwerfen und zu implementieren, das die Steuerung eines Roboterarms über mehrere Systemkomponenten hinweg ermöglicht.  
Besonderer Fokus lag auf der Trennung von Anwendung und Middleware, der Verwendung passender Architektur- und Entwurfsmuster sowie dem Treffen fundierter Designentscheidungen unter Berücksichtigung von Performance und Systemanforderungen.

## Aufgabenstellung
- Steuerung eines Roboterarms über ein verteiltes Softwaresystem
- Entwicklung einer Middleware zur Kommunikation zwischen Hardware und Anwendungsebene
- Umsetzung einer klaren Architekturtrennung zwischen **Middleware** und **Applikation**
- Nutzung des **MVC-Patterns** in der Anwendungsschicht
- Einsatz von **Remote Procedure Calls (RPC)** zur Interprozesskommunikation

## Anforderungen
- Entwurf einer robusten und skalierbaren Architektur für verteilte Systeme
- Implementierung von Kommunikationsschnittstellen über die Middleware
- Synchronisation und Zustandsverwaltung des Roboterarms
- Erstellung eines modularen Softwaredesigns für einfache Erweiterbarkeit
- Durchführung von Architekturentscheidungen mit Blick auf Komplexität, Performance und Wartbarkeit

## Vorgehensweise & Methoden
- Vorgehensmodell: Abgeschwächte Form des **V-Modells** (Analyse, Design, Implementierung, Test)
- Projektmanagement: Agile Arbeitsweise mit **Kanban** (GitLab Issue Boards)
- Architektur- und Entwurfsmuster: **MVC**, Schichtenarchitektur, Trennung von Middleware und Applikation
- Kollaborative Entwicklung mit **GitLab** (Versionskontrolle, Merge Requests, Code Reviews)

## Technologien & Tools
- **Programmiersprachen:** C / Java (ggf. anpassen)
- **Architekturmuster:** MVC, Schichtenarchitektur
- **Kommunikation:** RPC
- **Projektmanagement:** GitLab Issue Boards (Kanban)
- **Vorgehensmodell:** V-Modell (angepasst)

## Table of Content
- [Application (Arc42)](/Docs/Application/)
  - [Source IO](/IO/)
  - [Source Core](/Core/)
  - [Source MoveAdapter](/Core/)
  - [Source StateService](/Core/)
  - [Source Controller](/Core/)
  - [Source View](/View/)
  - [Source ActuatorController](/ActuatorController/)
- [Middleware (Arc42)](/Docs/Middleware/)
  - [Source Middleware Library C](/IO/)
  - [Source Middleware Library Java](/Middleware/)
  - [Source DNS](/Middleware/)
  - [Source Watchdog](/Middleware/)
- [Weekly Protocol](/Protocol/)
  
## Lizenz
Dieses Projekt wurde im Rahmen einer Hochschulveranstaltung erstellt und dient ausschließlich Demonstrations- und Lernzwecken.


