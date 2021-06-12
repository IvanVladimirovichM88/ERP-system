$(function() {
    $("#sortable").sortable();
    $("#sortable").disableSelection();
});

let _runEffect;
$(function() {
    function runEffect() {
        $("#effect").effect("drop", {}, 500, callback);
    };

    function callback() {
        $("#effect").removeAttr("style").hide().fadeIn();
    };

    _runEffect = function() {
        runEffect();
        return false;
    }
//    $("#button-save").on("click", function() {
//        runEffect();
//        return false;
//    });
});

counter = new class {

    constructor() {
        this.cnt = 1;
    }

    inc() {
        return this.cnt++
    }
}

String.prototype.isBlank = function() {
    return this.trim() === "";
}

class OpEntry {

    static WORKCELLS;
    static OPERATIONS;

    constructor(dbId, workcell, opName, qty, turn, params) {
        this.technologyId = document.getElementById("sortable").getAttribute("technology-id");
        this.id = dbId;
        this.workcell = workcell;
        this.opName = opName;
        this.qty = qty;
        this.turn = turn;
        if (params) {
            this.params = params.slice();
        } else {
            this.params = [];
        }
    }

    createWidget() {
        let w = OpEntry.createWidget();
        let inputs = OpEntry.getInputs(w);

        inputs.setDbId(this.id);
        inputs.workcellSelect.value = this.workcell;
        inputs.opSelect.value = this.opName;
        inputs.qtyInput.value = this.qty;
        inputs.numberContainer.value = this.turn;

        for (let i = 0; i < this.params.length; ++i) {
            inputs.paramInputs[i].tagInput.value = this.params[i].tag;
            inputs.paramInputs[i].valueInput.value = this.params[i].value;
        }

        return w;
    }

    async post() {
        console.log(this);
        let response = await fetch(
            `${applicationUrl}/api/operation_entries/post`, {
                method: "POST",
                body: JSON.stringify(this),
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        return response;
    }

    static async init() {
        this.WORKCELLS = await OpEntry.fetchWorkcells();
        this.OPERATIONS = await OpEntry.fetchOperations();
    }

    static async fetchAll() {
        return await fetch(
                `${applicationUrl}/api/operation_entries`, {
                    method: "GET",
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
            .then(resp => resp.json());
    }

    static fetchWorkcells() {
        return fetch(
                `${applicationUrl}/api/workcells`, {
                    method: "GET",
                    mode: "cors",
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
            .then(resp => resp.json());
    }

    static fetchOperations() {
        return fetch(
                `${applicationUrl}/api/operations`, {
                    method: "GET",
                    mode: "cors",
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
            .then(resp => resp.json());
    }

    static fromWidget(w) {
        let opEntry = new OpEntry();

        opEntry.id = w.getAttribute("db-id");
        opEntry.workcell = w.getElementsByClassName("workcell-select")[0].value;
        opEntry.opName = w.getElementsByClassName("op-select")[0].value;
        opEntry.qty = w.getElementsByClassName("qty-input")[0].value;
        opEntry.turn = w.getElementsByClassName("number-container")[0].textContent.slice(1);

        let paramWidgets = w.getElementsByClassName("op-param-widget");

        for (let i = 0; i < paramWidgets.length; ++i) {
            let param = OpParam.fromWidget(paramWidgets[i]);

            if (param !== null) {
                opEntry.params.push(param);
            }
        }

        return opEntry;
    }

    static fromAllWidgets() {
        let ws = OpEntry.findAllWidgets();
        let objects = [];

        for (let i = 0; i < ws.length; ++i) {
            if (ws[i].id.includes("prototype")) {
                continue
            };
            objects.push(OpEntry.fromWidget(ws[i]));
        }

        return objects;
    }

    static createWidget() {
        let prototype = document.getElementById("op-entry-prototype");
        let clone = prototype.cloneNode(true);
        clone.id = counter.inc();
        clone.setAttribute("db-id", "");
        clone.style.display = "block";

        let inputs = OpEntry.getInputs(clone);
        for (let i = 0; i < OpEntry.WORKCELLS.length; ++i) {
            let option = document.createElement("option");
            option.text = this.WORKCELLS[i].name;
            inputs.workcellSelect.appendChild(option);
        }
        for (let i = 0; i < OpEntry.OPERATIONS.length; ++i) {
            let option = document.createElement("option");
            option.text = OpEntry.OPERATIONS[i].name;
            inputs.opSelect.appendChild(option);
        }

        let opParamContainer = OpParam.createContainer();
        clone.appendChild(opParamContainer);

        for (let i = 0; i < 10; i++) {
            opParamContainer.appendChild(OpParam.createWidget());
        }

        return clone;
    }

    static createWidgets(objArray) {
        objArray.sort((o1, o2) => o1.turn - o2.turn);
        let ws = []
        for (let i = 0; i < objArray.length; ++i) {
            objArray[i].__proto__ = OpEntry.prototype;
            let w = objArray[i].createWidget()
            ws.push(w);
        }
        return ws;
    }

    static insertNewWidget() {
        let widget = OpEntry.createWidget();
        document.getElementById("sortable").appendChild(widget);
        OpEntry.updateNumbers();
        return widget;
    }

    static insertWidget(widget) {
        document.getElementById("sortable").appendChild(widget);
        OpEntry.updateNumbers();
        return widget;
    }

    static deleteWidget(child) {
        let parent = child.parentElement;
        if (parent.className.includes("op-entry-widget") || parent === document) {
            parent.remove();
            return;
        }
        this.deleteWidget(parent);
    }

    static updateNumbers() {
        let lis = document
            .getElementById("sortable")
            .getElementsByClassName("op-entry-widget");
        for (let i = 0; i < lis.length; ++i) {
            let number = lis[i].getElementsByClassName("number-container")[0];
            number.textContent = '#' + (i + 1);
        }
    }

    static findAllWidgets() {
        return document.getElementsByClassName("op-entry-widget");
    }

    static getInputs(widget) {
        let inputs = {};

        inputs.self = widget;
        inputs.getDbIg = () => inputs.self.getAttribute("db-id");
        inputs.setDbId = (value) => inputs.self.setAttribute("db-id", value);

        inputs.workcellSelect = widget.getElementsByClassName("workcell-select")[0];
        inputs.opSelect = widget.getElementsByClassName("op-select")[0];
        inputs.numberContainer = widget.getElementsByClassName("number-container")[0];
        inputs.qtyInput = widget.getElementsByClassName("qty-input")[0];
        inputs.paramInputs = [];

        let paramWidgets = widget.getElementsByClassName("op-param-widget");
        for (let i = 0; i < paramWidgets.length; ++i) {
            let pw = paramWidgets[i];
            inputs.paramInputs.push({
                tagInput: pw.getElementsByClassName("op-param-tag-input")[0],
                valueInput: pw.getElementsByClassName("op-param-value-input")[0]
            });
        }

        return inputs;
    }

}

class OpParam {

    constructor(tag, value) {
        this.tag = tag;
        this.value = value;
    }

    static fromWidget(widget) {
        let tag = OpParam.getTag(widget);
        let value = OpParam.getValue(widget);

        if (tag.isBlank()) {
            return null;
        }

        return new OpParam(tag, value);
    }

    static createWidget() {
        let prototype = document.getElementById("op-param-prototype");
        let clone = prototype.cloneNode(true);
        clone.id = counter.inc();
        clone.style.display = "flex";
        return clone;
    }

    static createContainer() {
        let prototype = document.getElementById("op-param-container-prototype");
        let clone = prototype.cloneNode(true);
        clone.id = counter.inc();
        clone.style.display = "block";
        return clone;
    }

    static getTag(widget) {
        return widget.getElementsByClassName("op-param-tag-input")[0].value;
    }

    static getValue(widget) {
        return widget.getElementsByClassName("op-param-value-input")[0].value;
    }

}

class Technology {

    static WIDGET = document.getElementById("sortable");

    constructor() {
        this.id = "";
        this.technologistId = undefined;
        this.opEntries = [];
    }

    render() {
        let oes = this.opEntries;
        oes.sort((o1, o2) => o1.turn - o2.turn);

        for (let i = 0; i < oes.length; ++i) {
            oes[i].__proto__ = OpEntry.prototype;
            let w = oes[i].createWidget()
            OpEntry.insertWidget(w)
        }

        Technology.WIDGET.setAttribute("technology-id", this.id);
        Technology.WIDGET.setAttribute("technologist-id", this.technologistId);
    }

    scan() {
        this.id = Technology.WIDGET.getAttribute("technology-id");
        this.technologistId = Technology.WIDGET.getAttribute("technologist-id");
        this.opEntries = OpEntry.fromAllWidgets();
    }

    clearScreen() {
        Technology.WIDGET.innerHTML = "";
    }

    async get(id) {
        this.clearScreen();
        const response = await fetch(
                `${applicationUrl}/api/technology/get/${id}`, {
                    method: "GET",
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            )
            .then(resp => resp.json())
            .then(obj => {
                this.id = obj.id;
                this.technologistId = obj.technologistId;
                this.opEntries = obj.opEntries;
                this.render();
            })
    }

    async post() {
        this.scan();
        if (this.opEntries.length === 0) {
            alert("Нельзя сохранить пустую технологию");
            return null;
        }
        let response = await fetch(
                `${applicationUrl}/api/technology/post`, {
                    method: "POST",
                    body: JSON.stringify(this),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
            .then(resp => resp.json())
            .then(obj => {
                this.id = obj.id;
                this.technologistId = obj.technologistId;
                this.opEntries = obj.opEntries;
                if (window.location.pathname.includes("add")) {
                    window.location.replace(`${applicationUrl}/technologies/edit/${this.id}`);
                } else if (window.location.pathname.includes("edit")) {
                    this.clearScreen();
                    this.render();
                    _runEffect();
                } else {
                    throw new Error("Запрос на сохранение технологии отправлен с неизвестного URL.");
                }
            });
    }

}