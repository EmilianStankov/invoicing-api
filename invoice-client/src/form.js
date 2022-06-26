import * as React from 'react';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';

export default function InvoiceForm(props) {
  return (
    <React.Fragment>
      <Typography variant="h6" gutterBottom>
        Configuration
      </Typography>
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <TextField
            required
            id="exchangeRates"
            name="exchangeRates"
            label="Exchange Rates"
            fullWidth
            variant="standard"
            value={props.exchangeRates}
            onChange={e => props.setExchangeRates(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            required
            id="outputCurrency"
            name="outputCurrency"
            label="Output Currency"
            fullWidth
            variant="standard"
            value={props.outputCurrency}
            onChange={e => props.setOutputCurrency(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <Button
            variant="contained"
            component="label"
          >
            Upload CSV
            <input
              type="file"
              ref={props.file}
              hidden
            />
          </Button>
        </Grid>
        <Grid item xs={12}>
          <TextField
            id="customerVat"
            name="customerVat"
            label="Customer VAT"
            fullWidth
            variant="standard"
            value={props.customerVat}
            onChange={e => props.setCustomerVat(e.target.value)}
          />
        </Grid>
      </Grid>
    </React.Fragment>
  );
}