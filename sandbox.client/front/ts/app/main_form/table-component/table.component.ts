import {Component, EventEmitter, Input, OnInit} from "@angular/core";
import {ClientRecord} from "../../../model/ClientRecord";

@Component({
    selector: 'my-table',
    template: require('./table.component.html'),
    styles: [require('./table.component.css')]
})

export class TableComponent implements OnInit {
    @Input() displayKeys: Array<string>;
    @Input() items: Array<ClientRecord>;
    @Input() onRemove: EventEmitter<boolean>;

    ngOnInit(): void {
    }
}
