import {Component, EventEmitter, Output, Input} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientDetails} from "../../model/ClientDetails";
import {isUndefined} from "util";
import {assertClassMethod} from "babel-types";
import {CharmRecord} from "../../model/CharmRecord";

@Component({
    selector: 'modal-view-component',
    template: require('./modal_view_component.html'),
    styles: [require ('./modal_view_component.css')],
})

export class ModalViewComponent {
    @Output() exit = new EventEmitter<void>();
    @Output() detailsFilled = new EventEmitter<ClientDetails>();

    @Input()selectedID: string;
    @Input()actionType: string;
    @Input()modalFormTitle: string;
    clientDetails: ClientDetails = new ClientDetails();
    charmRecords: CharmRecord[];
    isRemovable: Boolean[];

    constructor(private httpService: HttpService) {}

    ngOnInit() {
        this.loadCharms();
        this.loadClientDetails();
    }
                                      
    loadClientDetails() {
        this.clientDetails.clearPar();
        this.isRemovable = [true];

        if (this.actionType == "edit") {
            let url = "/client/clientDetails/" + this.selectedID;
            this.httpService.get(url).toPromise().then(result => {
                this.clientDetails = ClientDetails.from(result.json() as ClientDetails);
                if (this.clientDetails.homePhone.length == 0) {
                    this.clientDetails.homePhone = [""];
                }
                if (this.clientDetails.workPhone.length == 0) {
                    this.clientDetails.workPhone = [""];
                }
            }, error => {
                console.log(error);
            });
        } else {
            this.clientDetails.workPhone = [""];
            this.clientDetails.homePhone = [""];
            this.clientDetails.mobilePhones = [""];
        }
    }

    loadCharms() {
        this.httpService.get("/client/charms").toPromise().then(result => {
            this.charmRecords = result.json();
        }, error => {
            console.log(error);
        });
    }

    editAddClicked() {
        if (this.clientDetails.birth_date.length > 10) {
            alert("Неверный формат даты рождения");
        } else
        if (this.fieldsFilledCorrectly()) {
            this.detailsFilled.emit(this.clientDetails);
        } else {
            alert("Заполните все необходимые поля, помеченные звездочкой");
        }
    }

    fieldsFilledCorrectly() : boolean {
        if (this.clientDetails.name != "" && this.clientDetails.surname != "" && this.clientDetails.gender != ""
            && this.clientDetails.birth_date != "" && this.clientDetails.charm != ""
            && this.clientDetails.rAdressStreet != "" && this.clientDetails.rAdressFlat != ""
            && this.clientDetails.rAdressHouse != "" && this.clientDetails.mobilePhones[0] != "") {
            return true;
        } else {
            return false;
        }
    }

    addNewWorkPhone() {
        if (this.clientDetails.workPhone[this.clientDetails.workPhone.length-1] != "") {
            this.clientDetails.workPhone.push("");
        }
    }
    addNewHomePhone() {
        if (this.clientDetails.homePhone[this.clientDetails.homePhone.length-1] != "") {
            this.clientDetails.homePhone.push("");
        }
    }
    addNewMobilePhone() {
        if (this.clientDetails.mobilePhones[this.clientDetails.mobilePhones.length-1] != "") {
            this.clientDetails.mobilePhones.push("");
        }
    }
    removeMobilePhone(i : number) {
        this.clientDetails.mobilePhones.splice(i, 1);
    }
    removeWorkPhone(i : number) {
        this.clientDetails.workPhone.splice(i, 1);
    }
    removeHomePhone(i : number) {
        this.clientDetails.homePhone.splice(i, 1);
    }

    trackByFn(index: any) {
        return index;
    }
}