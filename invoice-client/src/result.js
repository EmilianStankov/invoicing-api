import * as React from 'react';
import Typography from '@mui/material/Typography';
import Table from '@mui/material/Table';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TableCell from '@mui/material/TableCell';
import TableBody from '@mui/material/TableBody';

export default function InvoiceResult(props) {
  return (
    <React.Fragment>
      <Typography variant="h6" gutterBottom>
        Invoice Results
      </Typography>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell><b>Name</b></TableCell>
            <TableCell align="right"><b>Balance</b></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {props.customers.map((customer) => (
            <TableRow key={customer.name}>
              <TableCell>{customer.name}</TableCell>
              <TableCell align="right">{customer.balance} {props.currency}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </React.Fragment>
  );
}