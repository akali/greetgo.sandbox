import {Component, Inject, OnInit} from "@angular/core";
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDatepickerModule, MatDialogRef} from "@angular/material";
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
    @Inject(MAT_DIALOG_DATA) private data,
    private picker: MatDatepickerModule
  ) {}

  get phonesFormArray(): FormArray {
    return this.form.get('phones') as FormArray;
  }

  ngOnInit(): void {
    this.client = this.data.client;

    if (this.client.workPhone === undefined || this.client.workPhone === null) {
      this.client.workPhone = new ClientPhone(this.client.id, "", PhoneType.WORK);
    }
    if (this.client.homePhone === undefined || this.client.homePhone === null) {
      this.client.homePhone = new ClientPhone(this.client.id, "", PhoneType.HOME);
    }
    if (this.client.regAddress === undefined || this.client.regAddress === null) {
      this.client.regAddress = new ClientAddress(this.client.id, AddressType.REG, "", "", "");
    }
    if (this.client.factAddress === undefined || this.client.factAddress === null) {
      this.client.factAddress = new ClientAddress(this.client.id, AddressType.FACT, "", "", "");
    }

    console.log(this.client);
    this.form = this.formBuilder.group({
      id: [this.client.id],
      name: [this.client.name, Validators.required],
      surname: [this.client.surname, Validators.required],
      patronymic: this.client.patronymic,
      gender: [this.client.gender, Validators.required],
      birthDate: [new Date(this.client.birthDate), Validators.required],
      regAddressStreet: [this.client.regAddress.street, Validators.required],
      regAddressHouse: [this.client.regAddress.house, Validators.required],
      regAddressFlat: [this.client.regAddress.flat, Validators.required],
      factAddressStreet: this.client.factAddress.street,
      factAddressHouse: this.client.factAddress.house,
      factAddressFlat: this.client.factAddress.flat,
      workPhone: [this.client.workPhone.number],
      homePhone: [this.client.homePhone.number],
      charm: [this.client.charm, Validators.required],
      phones: this.formBuilder.array([]),
    });
    this.client.phones.map(value => this.mobilePhoneGroup(value.number)).forEach(value => {
      this.phonesFormArray.push(value);
      console.log(value);
    });
    if (this.client.charm !== undefined && this.client.charm !== null) {
      let curCharm = this.client.charms.filter(value => value.id == this.client.charm.id)[0];
      if (curCharm !== undefined && curCharm !== null)
        this.form.controls['charm'].setValue(curCharm);
    }
  }

  submit(form) {
    if (form.invalid) return;
    console.log('Submitting:', form.value);
    let result = form.value;
    result.workPhone = new ClientPhone(result.id, result.workPhone, PhoneType.WORK);
    result.homePhone = new ClientPhone(result.id, result.homePhone, PhoneType.HOME);
    let phones = [];
    result.phones.forEach(value => phones.push(new ClientPhone(result.id, value.number, PhoneType.MOBILE)));
    result.phones = phones;
    result.birthDate = result.birthDate.getTime();
    this.dialogRef.close(result);
  }

  public addNumber() {
    this.phonesFormArray.push(this.mobilePhoneGroup(''));
  }

  private mobilePhoneGroup(value: string) {
    return this.formBuilder.group({
      number: this.formBuilder.control(value, [
        Validators.required
      ])
    })
  }
}
