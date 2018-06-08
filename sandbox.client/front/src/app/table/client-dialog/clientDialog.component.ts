import {Component, Inject, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {ClientDetail} from "../../../model/ClientDetail";
import {GenderType} from "../../../model/GenderType";
import {ClientPhone} from "../../../model/ClientPhone";
import {ClientAddress} from "../../../model/ClientAddress";
import {AddressType} from "../../../model/AddressType";
import {PhoneType} from "../../../model/PhoneType";

@Component({
  templateUrl: './clientDialog.component.html',
  styleUrls: ['./clientDialog.component.css']
})
export class ClientDialogComponent implements OnInit {
  form: FormGroup;
  client: ClientDetail;
  genders = [GenderType.MALE, GenderType.FEMALE];

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<ClientDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data
  ) {}

  ngOnInit(): void {
    if (this.data.clientDetailObservable)
      this.data.clientDetailObservable.subscribe(value => {
        this.client = ClientDetail.copy(value.json());
        this.client = ClientDetail.copy({
          id: 5,
          name: "Aisultan",
          surname: "Kali",
          patronymic: "Amanzholuly",
          gender: GenderType.MALE,
          birthDate: 100500,
          regAddress: new ClientAddress(5, AddressType.REG, "AkhanSeri", "13a", "32"),
          factAddress: new ClientAddress(5, AddressType.REG, "AkhanSeri", "13a", "32"),
          phones: [new ClientPhone(5, "+77052105967", PhoneType.MOBILE)],
        });
        this.form = this.formBuilder.group(<any>this.client);
      });
    else {
      this.client = new ClientDetail();
      this.data.charmsObservable.subscribe(value => {
        this.client.setCharms(value.json());
        this.client = ClientDetail.copy({
          id: 5,
          name: "Aisultan",
          surname: "Kali",
          patronymic: "Amanzholuly",
          gender: GenderType.MALE,
          birthDate: 100500,
          regAddress: new ClientAddress(5, AddressType.REG, "AkhanSeri", "13a", "32"),
          factAddress: new ClientAddress(5, AddressType.REG, "AkhanSeri", "13a", "32"),
          phones: [new ClientPhone(5, "+77052105967", PhoneType.MOBILE)],
        });
        this.form = this.formBuilder.group(<any>this.client);
      });
    }
  }

  submit(form) {
    console.log(form.value);
    this.dialogRef.close(`${form.value.filename}`);
  }
}
