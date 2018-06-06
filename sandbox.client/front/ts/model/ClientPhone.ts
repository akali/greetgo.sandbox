import {PhoneType} from "./PhoneType";

export class ClientPhone {
    constructor(client: number, number: string, type: PhoneType) {
        this.client = client;
        this.number = number;
        this.type = type;
    }

    public client: number /* int */;
    public number: string;
    public type: PhoneType;
}