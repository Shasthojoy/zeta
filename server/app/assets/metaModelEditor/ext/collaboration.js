var collaboration = (function () {
    'use strict';

    var debug = true;
    var realTime = true;
    var webSocketUri = "ws://" + window.location.host + "/metaModelSocket/" + window.loadedMetaModel.uuid;
    var userUuid = window.loadedMetaModel.userUuid;
    var nonBatchEvents = ["add", "remove", "change:source", "change:target", "change:attrs", "change:embeds", "change:parent", "change:z", "change:smooth", "change:manhattan"];

    var graph;
    var paper;
    var socket;

    var messageQueue = [];

    var batchStarted = false;
    var batchEvent = null;
    var batchCells = [];

    var init = function init(_graph, _paper) {
        log("init");

        graph = _graph;
        paper = _paper;
        socket = createSocket();
        graph.on("all", onGraphEvent);
    };

    var createSocket = function createSocket() {
        log("createSocket");

        var socket = new WebSocket(webSocketUri);
        socket.onopen = onSocketOpen;
        socket.onclose = onSocketClose;
        socket.onError = onSocketError;
        socket.onMessage = onSocketMessage;
        return socket;
    };

    var onSocketOpen = function onSocketOpen() {
        log("onSocketOpen");
        getGraph();
    };

    var onSocketClose = function onSocketClose() {
        log("onSocketClose");
    };

    var onSocketError = function onSocketError() {
        log("onSocketError");
    };

    var onSocketMessage = function onSocketMessage(message) {
        log("onSocketMessage");

        updateGraph(JSON.parse(message));
    };

    var onGraphEvent = function onGraphEvent(eventName, cell, data, options) {
        log("onGraphEvent", eventName);

        if (options && options.remote) {
            return;
        }

        if (realTime) {
            notifyCellChanged(eventName, cell);
        } else {
            processEventNoRealTime(eventName, cell);
        }
    };

    var notifyCellChanged = function notifyCellChanged(eventName, cell) {
        log("notifyCellChanged", eventName);

        var message = {
            type: "cellChanged",
            userUuid: userUuid,
            data: {
                eventName: eventName,
                cell: cell
            }
        };

        messageQueue.push(message);
        send();
    };

    var processEventNoRealTime = function processEventNoRealTime(eventName, cell) {
        log("processEventNoRealTime", eventName);

        if (!isBatchEvent(eventName)) {
            notifyCellChanged(eventName, cell);
            return;
        }

        switch (eventName) {

            case "batch:start":
                batchStarted = true;
                break;

            case "batch:stop":
                if (batchEvent && batchCells.length) {
                    batchCells.forEach(function (cell) {
                        notifyCellChanged(batchEvent, cell);
                    });
                    batchEvent = null;
                    batchCells.length = 0;
                }
                batchStarted = false;
                break;

            default:
                if (batchStarted) {
                    batchEvent = eventName;
                    if (batchCells.indexOf(cell) === -1) {
                        batchCells.push(cell);
                    }
                } else {
                    notifyCellChanged(eventName, cell);
                }
                break;

        }
    };

    var updateGraph = function updateGraph(message) {
        log("updateGraph");

        if (message.error) {
            return;
        }

        var remoteCell = message.data.cell;
        var localCell = graph.getCell(remoteCell.id);

        switch (message.type) {
            case "cellChanged":

                switch (message.data.eventName) {
                    case "add":
                        graph.addCell(remoteCell, {remote: true});
                        break;

                    case "remove":
                        if (localCell) {
                            localCell.remove({
                                disconnectLinks: true,
                                remote: true
                            });
                        }
                        break;

                    default:
                        if (localCell) {
                            var attribute = message.data.eventName.substr("change:".length);
                            localCell.set(attribute, message.data.cell[attribute], {remote: true});
                        }
                        break;
                }

                break;

            case "getGraph":
                if (message.data.cells) {
                    graph.fromJSON(message.data, {remote: true});
                }
                break;
        }
    };

    var getGraph = function getGraph() {
        log("getGraph");

        var message = {
            type: "getGraph",
            userUuid: userUuid
        };

        messageQueue.push(message);
        send();
    };

    var send = function send() {
        log("send");

        if (socket.readyState !== socket.OPEN) {
            return;
        }

        var notSent = [];

        messageQueue.forEach(function (message) {
            if (!sendMessage(message)) {
                notSent.push(message);
            }
        });

        messageQueue.length = 0;

        notSent.forEach(function (message) {
            messageQueue.push(message);
        });

        if (messageQueue.length) {
            send();
        }
    };

    var sendMessage = function sendMessage(message) {
        log("sendMessage");

        if (socket.readyState !== socket.OPEN) {
            return false;
        }

        socket.send(JSON.stringify(message));
        return true;
    };

    var isBatchEvent = function isBatchEvent(eventName) {
        return nonBatchEvents.indexOf(eventName) === -1;
    };

    var log = function log(fnName, message, force) {
        if (debug || force) {
            message = message || "";
            console.log("[" + fnName + "] " + message);
        }
    };

    return {
        init: init
    };
})();